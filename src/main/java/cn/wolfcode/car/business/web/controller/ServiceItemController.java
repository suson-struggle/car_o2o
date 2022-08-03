package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/serviceItem")
public class ServiceItemController {
    //模板前缀
    private static final String prefix = "business/serviceItem/";

    @Autowired
    private IServiceItemService serviceItemService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:serviceItem:view")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    @RequiresPermissions("business:serviceItem:add")
    @RequestMapping("/addPage")
    public String addPage() {
        return prefix + "add";
    }

    @RequiresPermissions("business:serviceItem:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model) {
        model.addAttribute("serviceItem", serviceItemService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:serviceItem:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        return serviceItemService.query(qo);
    }

    //新增
    @RequiresPermissions("business:serviceItem:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ServiceItem serviceItem) {
        serviceItemService.save(serviceItem);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("business:serviceItem:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(ServiceItem serviceItem) {
        serviceItemService.update(serviceItem);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("business:serviceItem:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        serviceItemService.deleteBatch(ids);
        return AjaxResult.success();
    }

    // 上架
    @RequestMapping("/saleOn")
    @ResponseBody
    public AjaxResult saleOn(Long id) {
        serviceItemService.saleOn(id);

        return AjaxResult.success();
    }

    // 下架
    @RequestMapping("/saleOff")
    @ResponseBody
    public AjaxResult saleOff(Long id) {
        serviceItemService.saleOff(id);

        return AjaxResult.success();
    }

    // 审核页面
    @RequestMapping("/startAuditPage")
    public String startAuditPage(Model model, Long id) {
        // 根据 id 查询出服务单项数据
        ServiceItem serviceItem = serviceItemService.get(id);
        model.addAttribute("serviceItem", serviceItem);

        // 查询出所有角色是店长或者财务专员的用户数据
        List<User> directors = userService.queryByRoleKey("shopOwner");
        List<User> finances = userService.queryByRoleKey("financial");
        model.addAttribute("directors", directors);
        model.addAttribute("finances", finances);

        // 根据 bpmnType 到 bpmnInfo 表中查询出 bpmnInfo 数据
        BpmnInfo bpmnInfo = bpmnInfoService.queryByType("car_package").get(0);
        model.addAttribute("bpmnInfo", bpmnInfo);

        return prefix + "audit";
    }

    //开始审核
    @RequestMapping("/startAudit")
    @ResponseBody
    public AjaxResult startAudit(CarPackageAuditVO vo) {
        // 调用业务方法处理请求
        serviceItemService.startAudit(vo);

        return AjaxResult.success();
    }
}
