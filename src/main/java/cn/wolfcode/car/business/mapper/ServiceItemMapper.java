package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceItemMapper {
    int insert(ServiceItem serviceItem);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(ServiceItem serviceItem);

    ServiceItem selectByPrimaryKey(Long id);

    List<ServiceItem> selectAll();

    List<ServiceItem> selectForList(ServiceItemQuery qo);

    void changeSaleStatus(Long id, Integer saleStatus);

    void changeAuditStatus(@Param("id") Long id, @Param("status") Integer status);
}