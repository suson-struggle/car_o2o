package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IAppointmentService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Appointment> query(AppointmentQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Appointment get(Long id);


    /**
     * 保存
     * @param appointment
     */
    void save(Appointment appointment);

  
    /**
     * 更新
     * @param appointment
     */
    void update(Appointment appointment);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Appointment> list();

    /**
     * 到店
     * @param id
     */
    void arrivalShop(Long id);

    /**
     * 取消
     * @param id
     */
    void cancel(Long id);

    /**
     * 结算单
     * @param id 预约数据的 id
     * @return 结算单的 id
     */
    Long generateStatement(Long id);
}