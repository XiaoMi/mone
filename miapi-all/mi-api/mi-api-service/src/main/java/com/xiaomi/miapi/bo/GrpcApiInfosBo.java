package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class GrpcApiInfosBo implements Serializable {
    private Map<String, List<String>> grpcApiInfos;
    private String symbol;
    private String ip;
    private Integer port;
}
