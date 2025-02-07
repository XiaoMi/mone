package run.mone.m78.api.bo.multiModal.audio;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsrRecognizedRes {
    private Integer code;
    private String message;

    private AsrRecognizedData data;

    public static String generateCodeMsg(Integer code, String message) {
        AsrRecognizedRes res = AsrRecognizedRes.builder()
                .code(code)
                .message(message)
                .build();
        return new Gson().toJson(res);
    }

    public static String generateRecognizedData(AsrRecognizedData asrRecognizedData) {
        AsrRecognizedRes res = AsrRecognizedRes.builder()
                .code(0)
                .message("success")
                .data(asrRecognizedData)
                .build();
        return new Gson().toJson(res);
    }

    @Data
    @Builder
    public static class AsrRecognizedData {
        private Long startTime;
        private Long endTime;
        private String text;
        private Boolean isFinal; // 是否是一句话的最后一句
        private Boolean canSend; // 握手成功后可以发送语音数据
        private String voiceId; // 一次ws 唯一的标识
    }

}
