package run.mone.m78.service.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

/**
 * @author wmin
 * @date 2024/1/15
 */
@Data
@Builder
public class WordToVoiceReq {

    private String apiKey;

    private String zzToken;

    private String text;

    /**
     * 方言类型
     */
    private String dialect;

    private String model;

}
