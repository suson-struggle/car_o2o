package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StatementMapper {
    int insert(Statement statement);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(Statement statement);

    Statement selectByPrimaryKey(Long id);

    List<Statement> selectAll();

    List<Statement> selectForList(StatementQuery query);

    void deleteRelation(Long id);

    void changeAmount(@Param("statementId") Long statementItemId, @Param("totalAmount") BigDecimal totalAmount, @Param("totalCount") BigDecimal totalCount, @Param("discountAmount") BigDecimal discountAmount);

    void changePayStatus(@Param("statementId") Long statementId, @Param("status") Integer status, @Param("payId") Long payId);

    Statement queryByAppointmentId(Long appointmentId);
}