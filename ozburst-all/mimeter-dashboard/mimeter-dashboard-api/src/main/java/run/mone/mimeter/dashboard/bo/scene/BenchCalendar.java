package run.mone.mimeter.dashboard.bo.scene;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BenchCalendar implements Serializable {
    private List<BenchDate> benchDateList;
}
