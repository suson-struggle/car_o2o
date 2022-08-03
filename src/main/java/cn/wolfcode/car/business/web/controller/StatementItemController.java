package cn.wolfcode.car.business.web.controller;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.impl.StatementItemServiceImpl;
import cn.wolfcode.car.business.service.impl.StatementServiceImpl;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/business/statementItem")
public class StatementItemController {

    @Autowired
    private StatementServiceImpl statementService;

    @Autowired
    private StatementItemServiceImpl statementItemService;

    private static final String prefix = "business/statementItem/";

    @RequestMapping("/itemDetail")
    public String queryDetail(Model model, Long statementId) {
        // 根据 id 获取结算单的数据
        Statement statement = statementService.get(statementId);
        model.addAttribute("statement", statement);
        // 判断当前的状态是消费中还是已支付
        if (Statement.STATUS_CONSUME.equals(statement.getStatus())) {
            // 跳转到消费页面
            return prefix + "consume";
        } else {
            return prefix + "detail";
        }
    }

    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        return statementItemService.query(qo);
    }

    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestBody List<StatementItem> list) {
        statementItemService.saveItems(list);

        return AjaxResult.success();
    }

    //支付
    @RequestMapping("/pay")
    @ResponseBody
    public AjaxResult pay(Long statementId) {
        statementItemService.pay(statementId);

        return AjaxResult.success();
    }
}
