package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class BenchDate implements Serializable {
    @HttpApiDocClassDefine(value = "benchCalendar", description = "该日期压测次数", defaultValue = "3")
    private Integer dateBenchCount;
    @HttpApiDocClassDefine(value = "benchCalendar", description = "压测日期", defaultValue = "2022-12-06")
    private String benchDate;
    @HttpApiDocClassDefine(value = "benchCalendar", ignore = true,description = "时间戳", defaultValue = "")
    private Long timestamp;
}
