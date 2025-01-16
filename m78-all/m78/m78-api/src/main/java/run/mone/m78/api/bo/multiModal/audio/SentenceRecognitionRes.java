package run.mone.m78.api.bo.multiModal.audio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SentenceRecognitionRes {
    private int code;

    private String message;

    private String requestId;

    private String result;
}
