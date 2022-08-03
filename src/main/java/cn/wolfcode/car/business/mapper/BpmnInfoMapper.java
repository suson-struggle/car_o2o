package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;

import java.util.List;

public interface BpmnInfoMapper {
    int insert(BpmnInfo bpmnInfo);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(BpmnInfo bpmnInfo);

    BpmnInfo selectByPrimaryKey(Long id);

    List<BpmnInfo> selectAll();

    List<BpmnInfo> selectForList(BpmnInfoQuery query);

    List<BpmnInfo> queryByType(String type);
}