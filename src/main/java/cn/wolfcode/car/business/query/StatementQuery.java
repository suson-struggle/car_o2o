package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class StatementQuery extends QueryObject {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    // 手动提供结束时间的 get 方法，让结束时间 + 1 天
    public Date getEndTime() {
        // 当没有传结束时间时，直接返回空，避免后续语句出现空指针异常
        if (endTime == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}
