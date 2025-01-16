package run.mone.m78.api.bo.multiModal.audio;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OfflineAsrQueryResDTO implements Serializable {
    private int code;

    private String message;

    private Long taskId;

    private String requestId;

    private OfflineAsrRecognizedData data;

    @Data
    @Builder
    public static class OfflineAsrRecognizedData implements Serializable {
        private Float  audioDuration;

        private String result;
    }
}
