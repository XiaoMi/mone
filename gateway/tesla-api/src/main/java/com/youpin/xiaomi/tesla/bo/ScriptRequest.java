package com.youpin.xiaomi.tesla.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ScriptRequest implements Serializable {

    private String traceId;

    private Map<String, String> header;

    private String postParam;

    private Map<String, String> getParam;

    private Map<String, String> formParam;

    private Map<String, String> attachments;

    private String uid;

    private String ip;
}
