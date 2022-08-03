package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IServiceItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<ServiceItem> query(ServiceItemQuery qo);


    /**
     * 查单个`
     * @param id
     * @return
     */
    ServiceItem get(Long id);


    /**
     * 保存
     * @param serviceItem
     */
    void save(ServiceItem serviceItem);

  
    /**
     * 更新
     * @param serviceItem
     */
    void update(ServiceItem serviceItem);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<ServiceItem> list();

    /**
     * 上架
     * @param id 服务单项的 id
     */
    void saleOn(Long id);

    /**
     * 下架
     * @param id 服务单项的 id
     */
    void saleOff(Long id);

    /**
     * 开始审核
     * @param vo
     */
    void startAudit(CarPackageAuditVO vo);
}
