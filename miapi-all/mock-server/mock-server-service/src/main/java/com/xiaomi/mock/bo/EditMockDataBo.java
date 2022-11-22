package com.xiaomi.mock.bo;

import lombok.Data;

@Data
public class EditMockDataBo {
    String url;
    String mockData;
    String paramsMd5;
    Integer enable;
    String mockExpID;
    String proxyUrl;
    Integer useMockScript;
    String mockScript;
}
