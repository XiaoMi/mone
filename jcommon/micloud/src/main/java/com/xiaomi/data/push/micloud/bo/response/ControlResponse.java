package com.xiaomi.data.push.micloud.bo.response;

import lombok.Data;

import java.util.List;

@Data
public class ControlResponse {
    private int code;
    private String message;
    private String requestID;
    private List<Data> data;

    @lombok.Data
    public class Data {
        private String hostname;
        private String message;
        private boolean success;
    }
}
