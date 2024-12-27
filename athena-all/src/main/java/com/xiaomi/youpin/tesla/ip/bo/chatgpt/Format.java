package com.xiaomi.youpin.tesla.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 21:32
 */
@Data
@Builder
public class Format {

    @Builder.Default
    private String type = "json_object";
}
