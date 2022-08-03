package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;

import java.util.List;

public interface AppointmentMapper {
    int insert(Appointment appointment);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(Appointment appointment);

    Appointment selectByPrimaryKey(Long id);

    List<Appointment> selectAll();

    List<Appointment> selectForList(AppointmentQuery query);

    void changeStatus(Long id, Integer status);
}