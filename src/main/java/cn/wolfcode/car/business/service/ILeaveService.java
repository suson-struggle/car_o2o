package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.business.vo.LeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface ILeaveService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Leave> query(LeaveQuery qo);

    /**
     * 发起审核
     * @param vo
     */
    void startAudit(LeaveVO vo);

    /**
     * 审批
     * @param id
     * @param status
     * @param info
     */
    void audit(Long id, Integer status, String info);
}
