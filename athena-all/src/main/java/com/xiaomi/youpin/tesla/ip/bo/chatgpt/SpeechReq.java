package com.xiaomi.youpin.tesla.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/11/24 11:45
 */
@Builder
@Data
public class SpeechReq implements Serializable {

    @Builder.Default
    private String model = "tts-1";

    private String input;

    @Builder.Default
    private String voice = "alloy";

}
