package run.mone.mimeter.dashboard.bo.scene;

import lombok.Data;

import java.io.Serializable;

@Data
public class GlobalHeader implements Serializable {
    private String headerName;
    private String headerValue;
}
