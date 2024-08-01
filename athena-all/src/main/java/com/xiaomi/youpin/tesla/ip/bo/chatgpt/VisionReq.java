package com.xiaomi.youpin.tesla.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/24 13:51
 */
@Data
@Builder
public class VisionReq {

    @Builder.Default
    private String model = "gpt-4-vision-preview";


    private List<ReqMessage> messages;


    @Builder.Default
    private int max_tokens = 300;


}
