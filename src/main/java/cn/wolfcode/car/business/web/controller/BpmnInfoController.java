package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 预约控制器
 */
@Controller
@RequestMapping("business/bpmnInfo")
public class BpmnInfoController {
    //模板前缀
    private static final String prefix = "business/bpmnInfo/";

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:bpmnInfo:view")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:bpmnInfo:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        return bpmnInfoService.query(qo);
    }

    //部署页面
    @RequestMapping("/deployPage")
    public String deployPage() {
        return prefix + "/deployPage";
    }

    //文件上传
    @RequestMapping("/uploadBpmnFile")
    @ResponseBody
    public AjaxResult uploadBpmnFile(MultipartFile file) throws IOException {
        Object path = bpmnInfoService.uploadBpmnFile(file);
        return AjaxResult.success(path);
    }

    //部署流程定义
    @RequestMapping("/deploy")
    @ResponseBody
    public AjaxResult deploy(String path, String bpmnType, String info) throws IOException {
        bpmnInfoService.deploy(path, bpmnType, info);

        return AjaxResult.success();
    }

    //删除流程定义
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id) {
        bpmnInfoService.delete(id);

        return AjaxResult.success();
    }

    //获取流程图
    @RequestMapping("/readResource")
    @ResponseBody
    public void readResource(String deploymentId, String type, HttpServletResponse response) throws IOException {
        InputStream inputStream =  bpmnInfoService.readResource(deploymentId, type);

        // 作用和 FileCopyUtils.copy 一致，也是将一个输入流拷贝到一个输出流中
        IOUtils.copy(inputStream, response.getOutputStream());
    }
}
