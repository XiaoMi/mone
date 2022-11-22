package com.xiaomi.mock.bo;

import lombok.Data;

@Data
public class MockProxyBo {
    private String originUrl;
    private String newUrl;
    private String paramMd5;
}
