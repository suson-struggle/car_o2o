package cn.wolfcode.car.business.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class LeaveVO {
    private String name;
    private Long mgr;
    private Long hr;
    private Long bpmnInfoId;
    private String reason;
    private String info;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
