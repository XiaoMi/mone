package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class GrpcApiInfosBo implements Serializable {
    private Map<String, List<String>> grpcApiInfos;
    private String symbol;
    private String ip;
    private Integer port;
}
