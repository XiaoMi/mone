package com.xiaomi.youpin.tesla.ip.bo.chatgpt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/26 19:38
 */
@Data
@Builder
public class Completions implements Serializable {


    @Builder.Default
    private String model = "gpt4_o";


    private List<Message> messages;


    @Builder.Default
    private boolean stream = true;

    @Builder.Default
    private double temperature = 0.2;

    @Builder.Default
    private Integer n = 1;

    //可以控制是否是json返回结果
    private Format response_format;


    //参数
    private String params;


}
