package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementItemServiceImpl implements IStatementItemService {

    @Autowired
    private StatementItemMapper statementItemMapper;

    @Autowired
    private StatementMapper statementMapper;

    @Override
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<StatementItem>(statementItemMapper.selectForList(qo));
    }

    public void saveItems(List<StatementItem> list) {
        // 判空
        if (list == null) {
            throw new BusinessException("您还没有选择数据！");
        }

        // 获取结算单 id 和折扣价
        StatementItem statementItem = list.remove(list.size() - 1);
        Long statementItemId = statementItem.getStatementId();
        BigDecimal discountAmount = statementItem.getItemPrice();

        // 在保存之前，需要先进行删除操作
        statementMapper.deleteRelation(statementItemId);

        // 计算总金额、总数量
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCount = BigDecimal.ZERO;

        // 将结算单数据保存到中间表中
        for (StatementItem item : list) {
            totalAmount = totalAmount.add(item.getItemPrice().multiply(item.getItemQuantity()));
            totalCount = totalCount.add(item.getItemQuantity());
            statementItemMapper.insert(item);
        }

        // 修改结算单数据中的总金额、总数量、折扣金额
        statementMapper.changeAmount(statementItemId, totalAmount, totalCount, discountAmount);
    }

    public void pay(Long statementId) {
        // 根据 id 获取结算单数据
        Statement statement = statementMapper.selectByPrimaryKey(statementId);
        // 判断当前的状态是否为结算中，不是则抛出异常
        if (!Statement.STATUS_CONSUME.equals(statement.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 修改结算单数据，收款人，支付时间，支付状态设置为已支付
        statementMapper.changePayStatus(statementId, Statement.STATUS_PAID, ShiroUtils.getUser().getId());
    }
}
