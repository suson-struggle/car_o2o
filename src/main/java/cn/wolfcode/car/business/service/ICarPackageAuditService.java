package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.io.InputStream;

/**
 * 岗位服务接口
 */
public interface ICarPackageAuditService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo);

    /**
     * 审核进度
     * @param id
     * @return
     */
    InputStream processImg(Long id);

    /**
     * 撤销
     * @param id
     */
    void cancelApply(Long id);

    /**
     * 审核
     * @param id
     * @param auditStatus
     * @param info
     */
    void audit(Long id, Integer auditStatus, String info);
}
