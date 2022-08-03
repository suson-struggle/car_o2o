package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.business.service.ILeaveService;
import cn.wolfcode.car.business.vo.LeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 预约控制器
 */
@Controller
@RequestMapping("business/leave")
public class LeaveController {
    //模板前缀
    private static final String prefix = "business/leave/";

    @Autowired
    private ILeaveService leaveService;
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Autowired
    private IUserService userService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:leave:view")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:leave:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Leave> query(LeaveQuery qo) {
        return leaveService.query(qo);
    }

    //发起申请页面
    @RequestMapping("/startAuditPage")
    public String startAuditPage(Model model) {
        // 往作用域中存入值
        // 审批人信息
        List<User> mgrs = userService.queryByRoleKey("mgr");
        List<User> hrs = userService.queryByRoleKey("hr");
        model.addAttribute("mgrs", mgrs);
        model.addAttribute("hrs", hrs);

        // 流程定义名称
        List<BpmnInfo> bpmnInfos = bpmnInfoMapper.queryByType("sick-leave");
        model.addAttribute("bpmnInfo", bpmnInfos.get(0));
        return prefix + "/startAuditPage";
    }

    // 发起审批
    @RequestMapping("/startAudit")
    @ResponseBody
    public AjaxResult startAudit(LeaveVO vo) {
        leaveService.startAudit(vo);

        return AjaxResult.success();
    }

    // 我的已办
    @RequestMapping("/todoPage")
    public String todoPage() {
        return prefix + "/todoPage";
    }

    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<Leave> todoQuery(LeaveQuery qo) {
        qo.setAuditorId(ShiroUtils.getUserId());
        qo.setStatus(Leave.STATUS_IN_ROGRESS);
        return leaveService.query(qo);
    }

    //审批页面
    @RequestMapping("/auditPage")
    public String auditPage(Model model, Long id) {
        model.addAttribute("id", id);
        return prefix + "/auditPage";
    }

    //审批
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id, Integer auditStatus, String info) {
        leaveService.audit(id, auditStatus, info);
        return AjaxResult.success();
    }

    //我的已办
    @RequestMapping("/donePage")
    public String donePage() {
        return prefix + "donePage";
    }

    @RequestMapping("/doneQuery")
    @ResponseBody
    public TablePageInfo<Leave> doneQuery(LeaveQuery qo) {
        qo.setName(ShiroUtils.getUser().getUserName());
        return leaveService.query(qo);
    }
}
