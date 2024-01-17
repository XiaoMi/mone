package run.mone.mimeter.dashboard.bo;

import lombok.Data;

import java.util.List;

@Data
public class DubboServiceList {
    private List<DubboService> serviceList;
}
