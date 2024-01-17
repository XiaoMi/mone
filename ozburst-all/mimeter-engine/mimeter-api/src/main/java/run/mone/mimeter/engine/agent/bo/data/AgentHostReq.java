package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgentHostReq implements Serializable {
    String domain;

    String ip;
}
