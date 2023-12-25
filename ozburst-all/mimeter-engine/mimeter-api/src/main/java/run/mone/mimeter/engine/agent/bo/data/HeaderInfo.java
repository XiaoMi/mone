package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeaderInfo implements Serializable {
    private String headerName;
    private String headerValue;
}
