package run.mone.m78.api.bo.multiModal.audio;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineAsrQueryReqDTO implements Serializable {
    private Long taskId;
}
