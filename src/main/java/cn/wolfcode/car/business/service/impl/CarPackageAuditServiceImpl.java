package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Service
@Transactional
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    @Autowired
    private TaskService taskService;

    @Override
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<CarPackageAudit>(carPackageAuditMapper.selectForList(qo));
    }

    @Override
    public InputStream processImg(Long id) {
        // 根据 id 查询出审核进度
        CarPackageAudit carPackageAudit = carPackageAuditMapper.selectByPrimaryKey(id);

        List<String> activeHighList = new ArrayList<>();
        // 判断进度是否是审核中，如果不是则抛出异常
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            // 获取高亮节点列表
            activeHighList = runtimeService.getActiveActivityIds(carPackageAudit.getInstanceId());
        } else {
            activeHighList = Collections.EMPTY_LIST;
        }

        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(carPackageAudit.getBpmnInfoId());

        // 响应 png 格式数据
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        InputStream inputStream =  generator.generateDiagram(repositoryService.getBpmnModel(bpmnInfo.getActProcessId()),
                activeHighList,
                Collections.EMPTY_LIST, "宋体", "宋体", "宋体");

        return inputStream;
    }

    @Override
    public void cancelApply(Long id) {
        // 1、根据 id 获取数据
        CarPackageAudit carPackageAudit = carPackageAuditMapper.selectByPrimaryKey(id);
        // 2、如果不是审核中的状态则抛出异常
        if (!CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 3、修改状态和删除数据
        // 修改服务单项状态为初始化
        serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(), ServiceItem.AUDITSTATUS_REPLY);
        // 修改 carPackageAudit 状态为审核取消
        carPackageAuditMapper.changeStatus(carPackageAudit.getId(), CarPackageAudit.STATUS_CANCEL);
        // 删除流程定义
        runtimeService.deleteProcessInstance(carPackageAudit.getInstanceId(), "就要删除");
    }

    @Override
    @Transactional
    public void audit(Long id, Integer auditStatus, String info) {
        // 根据 id 查询 carPackageAudit 的数据
        CarPackageAudit carPackageAudit = carPackageAuditMapper.selectByPrimaryKey(id);
        // 获取状态，如果不是审核中就抛出异常
        if (!CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 获取节点
        Task task = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
        // 设置流程变量
        Map<String, Object> map = new HashMap<>();
        map.put("auditStatus", auditStatus);    // 设置审核状态
        // 审批当前节点，并设置流程变量
        taskService.complete(task.getId(), map);
        // 判断是同意还是拒绝
        if (CarPackageAudit.STATUS_PASS.equals(auditStatus)) {
            // 同意
            // 获取当前任务节点
            Task newTask = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
            // 判断当前节点是否为空
            if (newTask != null) {
                // 有下一个节点
                // 获取审核人名称并设置到 carPackageAudit 中，并设置最后审批时间和备注
                carPackageAudit.setAuditorId(Long.parseLong(newTask.getAssignee()));
            } else {
                // 没有下一个节点
                // 更改服务单项的审核状态为审核通过
                serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(), ServiceItem.AUDITSTATUS_APPROVED);
                carPackageAudit.setStatus(CarPackageAudit.STATUS_PASS);
            }
            // 更改 carPackageAudit 的状态为审核通过，设置最后审核时间，备注信息
            carPackageAudit.setInfo(carPackageAudit.getInfo() + "--->审核人：[" + ShiroUtils.getUser().getUserName() + "]-通过-备注：[" + info + "]");
        } else {
            // 拒绝
            // 更改服务单项的审核状态为重新调整
            serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(), ServiceItem.AUDITSTATUS_REPLY);
            // 更改 carPackageAudit 的状态为审核拒绝，设置最后审核时间，备注信息
            carPackageAudit.setStatus(CarPackageAudit.STATUS_REJECT);
            carPackageAudit.setInfo(carPackageAudit.getInfo() + "--->审核人：[" + ShiroUtils.getUser().getUserName() + "]-拒绝-备注：[" + info + "]");
        }

        carPackageAudit.setAuditTime(new Date());
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }
}
