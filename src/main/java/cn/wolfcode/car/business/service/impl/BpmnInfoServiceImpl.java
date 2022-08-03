package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.file.FileUploadUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BpmnInfoServiceImpl implements IBpmnInfoService {

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BpmnInfo>(bpmnInfoMapper.selectForList(qo));
    }

    @Override
    public String uploadBpmnFile(MultipartFile file) throws IOException {
        // 如果文件的不是 .bpmn 结尾的，则抛出异常
        String extension = FileUploadUtils.getExtension(file);

        if (!"bpmn".equals(extension)) {
            throw new BusinessException("必须上传 bpmn 格式的文件");
        }
        // 将文件上传到指定位置，并返回保存的路径
        return FileUploadUtils.upload(SystemConfig.getProfile(), file);
    }

    @Override
    public void deploy(String path, String bpmnType, String info) throws IOException {
        // 1、通过 path 找到磁盘中的流程图文件
        File file = new File(SystemConfig.getProfile(), path);

        // 2、调用 activiti 的 API 往数据库中存值
        FileInputStream inputStream = new FileInputStream(file);
        Deployment deployment = repositoryService
                .createDeployment()
                .addInputStream(path, inputStream)
                .deploy();

        // 关闭输入流
        inputStream.close();

        // 根据部署对象的 id 查询流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        // 3、在 bpmnInfo 表中存一份
        BpmnInfo bpmnInfo = new BpmnInfo();
        bpmnInfo.setInfo(info);
        bpmnInfo.setBpmnType(bpmnType);
        bpmnInfo.setDeploymentId(deployment.getId());
        bpmnInfo.setDeployTime(deployment.getDeploymentTime());
        bpmnInfo.setActProcessId(processDefinition.getId());
        bpmnInfo.setActProcessKey(processDefinition.getKey());
        bpmnInfo.setBpmnName(processDefinition.getName());
        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public void delete(Long id) {
        // 根据 id 获取 bpmnInfo 数据
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(id);

        // 删除 bpmnInfo 数据
        bpmnInfoMapper.deleteByPrimaryKey(id);

        // 获取流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(bpmnInfo.getDeploymentId())
                .singleResult();
        // 删除 Activiti 表中的流程定义数据
        repositoryService.deleteDeployment(bpmnInfo.getDeploymentId());

        // 删除流程定义文件
        File file = new File(SystemConfig.getProfile(), processDefinition.getResourceName());
        if (file.exists()) {
            file.delete();
        }

        // 如果有未完成的流程实例，通过业务标识将它们的审核状态改成初始化
    }

    @Override
    public InputStream readResource(String deploymentId, String type) {
        // 获取流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();

        InputStream inputStream = null;
        // 判断类型是 xml 还是图片
        if ("xml".equals(type)) {
            // 响应 xml 格式数据
            inputStream = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        } else {
            // 响应 png 格式数据
            DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            inputStream =  generator.generateDiagram(repositoryService.getBpmnModel(processDefinition.getId()),
                    Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST, "宋体", "宋体", "宋体");
        }
        return inputStream;
    }

    @Override
    public List<BpmnInfo> queryByType(String type) {
        return bpmnInfoMapper.queryByType(type);
    }

}
