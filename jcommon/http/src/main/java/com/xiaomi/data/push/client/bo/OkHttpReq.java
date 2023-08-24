package com.xiaomi.data.push.client.bo;

import lombok.Data;
import okhttp3.Protocol;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/8/24 14:56
 */
@Data
public class OkHttpReq implements Serializable {

    private List<Protocol> protocolList;

}
