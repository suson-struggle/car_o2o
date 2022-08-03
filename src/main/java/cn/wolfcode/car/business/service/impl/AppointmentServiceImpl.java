package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private StatementMapper statementMapper;

    @Override
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }


    @Override
    public void save(Appointment appointment) {
        Appointment newAppointment = new Appointment();
        newAppointment.setAppointmentTime(appointment.getAppointmentTime());
        newAppointment.setCarSeries(appointment.getCarSeries());
        newAppointment.setCustomerName(appointment.getCustomerName());
        newAppointment.setCustomerPhone(appointment.getCustomerPhone());
        newAppointment.setInfo(appointment.getInfo());
        newAppointment.setLicensePlate(appointment.getLicensePlate());
        newAppointment.setServiceType(appointment.getServiceType());
        // 设置创建时间
        newAppointment.setCreateTime(new Date());
        appointmentMapper.insert(newAppointment);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {
        // 获取数据库中的数据
        Appointment oldAppointment = this.get(appointment.getId());
        // 判断当前的状态是否是预约中，如果不是则抛出异常
        if (!Appointment.STATUS_APPOINTMENT.equals(oldAppointment.getStatus())) {
            throw new BusinessException("非法操作");
        }

        // 判断是否有不需要修改的字段，在 SQL 语句中进行删除
        // 不需要修改的字段：创建时间、到店时间、状态

        // 更新数据
        appointmentMapper.updateByPrimaryKey(appointment);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 判断预约状态，只有预约中，用户取消，超时取消的预约数据才可以被删除
            Appointment appointment = this.get(dictId);
            if (!Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus()) ||
                    Appointment.STATUS_CANCEL.equals(appointment.getStatus()) ||
                    Appointment.STATUS_OVERTIME.equals(appointment.getStatus())) {
                throw new BusinessException("非法操作");
            }
            appointmentMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }

    @Override
    public void arrivalShop(Long id) {
        // 根据 id 查询数据
        Appointment appointment = this.get(id);
        // 判断状态是否是预约中
        if (!Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 修改状态为已到店，修改到店时间
        appointmentMapper.changeStatus(id, Appointment.STATUS_ARRIVAL);
    }

    @Override
    public void cancel(Long id) {
        // 根据 id 查询数据
        Appointment appointment = this.get(id);
        // 判断状态是否是预约中
        if (!Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 修改状态为已取消
        appointmentMapper.changeStatus(id, Appointment.STATUS_CANCEL);
    }

    @Override
    public Long generateStatement(Long id) {
        // 根据 id 获取数据
        Appointment appointment = this.get(id);
        // 去结算单表中查询是否有指定当前预约 id 的数据
        Statement statement =  statementMapper.queryByAppointmentId(id);
        // 如果没有，则创建新的结算单，数据来源于预约数据
        if (statement == null) {
            statement = new Statement();
            statement.setActualArrivalTime(appointment.getActualArrivalTime());
            statement.setServiceType(appointment.getServiceType());
            statement.setLicensePlate(appointment.getLicensePlate());
            statement.setInfo(appointment.getInfo());
            statement.setCustomerPhone(appointment.getCustomerPhone());
            statement.setCustomerName(appointment.getCustomerName());
            statement.setCarSeries(appointment.getCarSeries());
            statement.setCreateTime(new Date());
            statement.setAppointmentId(id);

            // 插入数据到数据库中
            statementMapper.insert(statement);
        }

        // 设置预约数据的状态为已结算
        appointmentMapper.changeStatus(id, Appointment.STATUS_SETTLE);

        return statement.getId();
    }
}
