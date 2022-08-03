package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementServiceImpl implements IStatementService {

    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;


    @Override
    public TablePageInfo<Statement> query(StatementQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Statement>(statementMapper.selectForList(qo));
    }


    @Override
    public void save(Statement statement) {
        // 创建一个新的对象，从用户传入的请求参数中获取需要的属性值
        Statement newStatement = new Statement();
        newStatement.setCreateTime(new Date());
        newStatement.setCarSeries(statement.getCarSeries());
        newStatement.setCustomerName(statement.getCustomerName());
        newStatement.setCustomerPhone(statement.getCustomerPhone());
        newStatement.setDiscountAmount(statement.getDiscountAmount());
        newStatement.setInfo(statement.getInfo());
        newStatement.setLicensePlate(statement.getLicensePlate());
        newStatement.setServiceType(statement.getServiceType());
        newStatement.setActualArrivalTime(statement.getActualArrivalTime());

        statementMapper.insert(newStatement);
    }

    @Override
    public Statement get(Long id) {
        return statementMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Statement statement) {
        // 从数据库中获取结算单数据
        Statement oldStatement = this.get(statement.getId());
        // 判断当前的状态是否是消费中，如果不是抛出异常
        if (!Statement.STATUS_CONSUME.equals(oldStatement.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 更改结算单数据，删除 SQL 语句中不需要修改的部分
        statementMapper.updateByPrimaryKey(statement);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 获取结算单数据
            Statement statement = this.get(dictId);
            // 判断结算单数据是否是消费中，如果不是则抛出异常
            if (!Statement.STATUS_CONSUME.equals(statement.getStatus())) {
                throw new BusinessException("非法操作");
            }
            // 删除结算单数据
            statementMapper.deleteByPrimaryKey(dictId);

            // 删除中间表数据
            statementMapper.deleteRelation(statement.getId());

            // 如果结算单的预约 id 不为空，则修改该预约 id 的状态回已到店，便于用户下次再通过该预约生成新的结算单
            if (statement.getAppointmentId() != null) {
                appointmentMapper.changeStatus(statement.getAppointmentId(), Appointment.STATUS_ARRIVAL);
            }
        }
    }

    @Override
    public List<Statement> list() {
        return statementMapper.selectAll();
    }
}
