package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IStatementItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<StatementItem> query(StatementItemQuery qo);
}
