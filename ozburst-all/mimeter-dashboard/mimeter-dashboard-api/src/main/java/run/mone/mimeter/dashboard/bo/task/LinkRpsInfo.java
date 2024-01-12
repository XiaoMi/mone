package run.mone.mimeter.dashboard.bo.task;

import lombok.Data;

import java.io.Serializable;

@Data
public class LinkRpsInfo implements Serializable {
    Integer linkId;
    Integer rps;

    public LinkRpsInfo(Integer linkId) {
        this.linkId = linkId;
    }
}
