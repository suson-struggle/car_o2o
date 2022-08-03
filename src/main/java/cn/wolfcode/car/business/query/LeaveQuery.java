package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class LeaveQuery extends QueryObject {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private Integer status;
    private String name;
    private Long auditorId;

    // 结束时间加 一天
    public Date getEndTime() {
        if (endTime == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}
