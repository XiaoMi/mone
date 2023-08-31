package com.xiaomi.data.push.client.bo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/8/24 14:56
 */
@Data
@Slf4j
public class OkHttpReq implements Serializable {

    private List<Protocol> protocolList;


    public OkHttpReq(String protocol) {
        addProtocol(protocol);
    }

    public OkHttpReq() {
    }

    public void addProtocol(String protocol) {
        if (null == protocol || protocol.trim().equals("")) {
            return;
        }
        if (null == protocolList) {
            protocolList = new ArrayList<>();
        }
        try {
            protocolList.add(Protocol.valueOf(protocol));
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            protocolList.add(Protocol.HTTP_1_1);
        }
    }

}
