package com.xiaomi.youpin.tesla.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 16:27
 */
@Data
public class ProxyAsk implements Serializable {

    private String id;

    private String token;

    private String zzToken;

    private String promptName;

    private String[] params;

    private Map<String, String> paramMap;

    private boolean skipSystemSetting;

    private String model;

    private int maxToken;

    /**
     * 是否开启测试模式
     */
    private boolean debug;

    private List<Msg> msgList;

    /**
     * 0 是单条的,并且传过来是promptName
     * 1 多条,就是正常的多轮问答
     */
    private int type;

    /**
     * 来源
     */
    private String from;

}
