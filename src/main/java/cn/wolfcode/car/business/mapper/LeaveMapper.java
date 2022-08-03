package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.query.LeaveQuery;

import java.util.List;

public interface LeaveMapper {
    int insert(Leave leave);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(Leave leave);

    Leave selectByPrimaryKey(Long id);

    List<Leave> selectAll();

    List<Leave> selectForList(LeaveQuery query);
}