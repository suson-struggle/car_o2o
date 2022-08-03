package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.LeaveMapper;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.business.service.ILeaveService;
import cn.wolfcode.car.business.vo.LeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class LeaveServiceImpl implements ILeaveService {

    @Autowired
    private LeaveMapper leaveMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Autowired
    private TaskService taskService;

    @Override
    public TablePageInfo<Leave> query(LeaveQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Leave>(leaveMapper.selectForList(qo));
    }

    @Override
    @Transactional
    public void startAudit(LeaveVO vo) {
        // 获取状态，判断是否是审核中的状态，不是就抛出异常
        // 封装 leave 对象
        Leave leave = new Leave();
        leave.setName(vo.getName());
        leave.setInfo(vo.getInfo());
        leave.setAuditId(vo.getMgr());
        leave.setBpmnInfoId(vo.getBpmnInfoId());
        leave.setStartTime(vo.getStartTime());
        leave.setEndTime(vo.getEndTime());
        leave.setReason(vo.getReason());
        leave.setStatus(Leave.STATUS_IN_ROGRESS);
        leave.setCreator(ShiroUtils.getUser());
        // 插入数据，生成 id
        leaveMapper.insert(leave);

        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(vo.getBpmnInfoId());

        Map<String, Object> map = new HashMap<>();
        map.put("mgr", vo.getMgr());
        map.put("hr", vo.getHr());
        // 开启流程实例，设置流程变量，绑定业务标识
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), leave.getId().toString(), map);

        leave.setInstanceId(processInstance.getId());
        leaveMapper.updateByPrimaryKey(leave);
    }

    @Override
    @Transactional
    public void audit(Long id, Integer status, String info) {
        // 查询流程，如果不是审核中就抛出异常
        Leave leave = leaveMapper.selectByPrimaryKey(id);
        if (!Leave.STATUS_IN_ROGRESS.equals(leave.getStatus())) {
            throw new BusinessException("非法操作");
        }

        // 获取当前运行的节点
        Task task = taskService.createTaskQuery().processInstanceId(leave.getInstanceId()).singleResult();

        // 完成任务
        taskService.complete(task.getId());

        // 判断是同意还是拒绝
        if (Leave.STATUS_PASS.equals(status)) {
            // 同意
            Task nextTask = taskService.createTaskQuery().processInstanceId(leave.getInstanceId()).singleResult();
            if (nextTask != null) {
                // 有下一个节点，说明审批没有结束，设置任务负责人
                leave.setAuditId(Long.parseLong(nextTask.getAssignee()));
            } else {
                // 没有下一个节点，流程结束
                leave.setStatus(Leave.STATUS_PASS);
            }

            leave.setInfo(leave.getInfo() + "--->审核人：【" + leave.getAuditor().getUserName() + "】--同意--备注：【" + info + "】");

        } else {
            // 拒绝
            // 删除流程实例
            runtimeService.deleteProcessInstance(leave.getInstanceId(), "就是要删除");
            // 修改状态为已删除
            leave.setStatus(Leave.STATUS_REJECT);
            leave.setInfo(leave.getInfo() + "--->审核人：【" + leave.getAuditor().getUserName() + "】--拒绝--备注：【" + info + "】");
        }
        leaveMapper.updateByPrimaryKey(leave);
    }
}
