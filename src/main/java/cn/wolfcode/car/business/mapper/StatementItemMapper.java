package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;

import java.util.List;

public interface StatementItemMapper {
    int insert(StatementItem statementItem);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(StatementItem statementItem);

    StatementItem selectByPrimaryKey(Long id);

    List<StatementItem> selectAll();

    List<StatementItem> selectForList(StatementItemQuery query);
}