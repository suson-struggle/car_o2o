package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarPackageAuditMapper {
    int insert(CarPackageAudit carPackageAudit);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(CarPackageAudit carPackageAudit);

    CarPackageAudit selectByPrimaryKey(Long id);

    List<CarPackageAudit> selectAll();

    List<CarPackageAudit> selectForList(CarPackageAuditQuery query);

    void changeStatus(@Param("id") Long id, @Param("status") Integer status);
}