package run.mone.m78.api.bo.multiModal.audio;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OfflineAsrResDTO implements Serializable {

    private int code;

    private String message;

    private Long taskId;

    private String requestId;
}
