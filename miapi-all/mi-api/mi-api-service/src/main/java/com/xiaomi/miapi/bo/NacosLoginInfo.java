package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class NacosLoginInfo {
    private String accessToken;
    private long tokenTtl;
    private boolean globalAdmin;
}
