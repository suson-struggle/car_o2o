package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarPackageAuditQuery extends QueryObject {
    private String creatorName;
    private Long auditorId;
    private Integer status;
    private String name;
}
