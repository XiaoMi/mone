package run.mone.mimeter.dashboard.bo;

import lombok.Data;

@Data
public class DubboService {
    private String name;
    private Integer healthyInstanceCount;
}
