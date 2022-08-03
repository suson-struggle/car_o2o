package cn.wolfcode.car.business.domain;

import cn.wolfcode.car.base.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Leave {
    public static final Integer STATUS_IN_ROGRESS = 0;  //审核中
    public static final Integer STATUS_REJECT = 1;      //审核拒绝
    public static final Integer STATUS_PASS = 2;        //审核通过
    public static final Integer STATUS_CANCEL = 3;      //审核撤销
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Long auditId;        // 审核人 id
    private User auditor;                   //当前审核人对象
    private Long bpmnInfoId;                //关联流程id
    private BpmnInfo bpmnInfo;              //关联流程定义对象
    private Integer status;
    private String info;
    private String instanceId;
    private User creator;   // 创建人
}