package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 预约控制器
 */
@Controller
@RequestMapping("business/carPackageAudit")
public class CarPackageAuditController {
    //模板前缀
    private static final String prefix = "business/carPackageAudit/";

    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    //页面------------------------------------------------------------
    //列表
    // @RequiresPermissions("business:carPackageAudit:view")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    //数据-----------------------------------------------------------
    //列表
    // @RequiresPermissions("business:carPackageAudit:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        // 设置创建人
        qo.setCreatorName(ShiroUtils.getUser().getUserName());
        return carPackageAuditService.query(qo);
    }

    //查看审核进度
    @RequestMapping("/processImg")
    @ResponseBody
    public void processImg(Long id, HttpServletResponse response) throws IOException {
        InputStream inputStream = carPackageAuditService.processImg(id);

        IOUtils.copy(inputStream, response.getOutputStream());
    }

    //撤销
    @RequestMapping("/cancelApply")
    @ResponseBody
    public AjaxResult cancelApply(Long id) {
        carPackageAuditService.cancelApply(id);

        return AjaxResult.success();
    }

    //我的待办
    @RequestMapping("/todoPage")
    public String todoPage() {
        return prefix + "todoPage";
    }

    //我的已办
    @RequestMapping("/donePage")
    public String donePage() {
        return prefix + "donePage";
    }

    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> todoQuery(CarPackageAuditQuery qo) {
        // 设置审核人 id 和状态
        qo.setAuditorId(ShiroUtils.getUserId());
        qo.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        return carPackageAuditService.query(qo);
    }

    @RequestMapping("/doneQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> doneQuery(CarPackageAuditQuery qo) {
        qo.setName(ShiroUtils.getUser().getUserName());
        return carPackageAuditService.query(qo);
    }

    //审核页面
    @RequestMapping("/auditPage")
    public String auditPage(Model model, Long id) {
        // 将套餐审核的 id 设置到作用域中
        model.addAttribute("id", id);
        return prefix + "auditPage";
    }

    //审核
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id, Integer auditStatus, String info) {
        carPackageAuditService.audit(id, auditStatus, info);

        return AjaxResult.success();
    }
}
