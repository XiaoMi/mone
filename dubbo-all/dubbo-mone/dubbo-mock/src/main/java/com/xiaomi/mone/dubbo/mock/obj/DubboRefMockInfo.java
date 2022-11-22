package com.xiaomi.mone.dubbo.mock.obj;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DubboRefMockInfo {

    private String interfaceName;

    private String group = "";

    private String version = "";

    private List<String> methods;

    private String mockUrlPrefix;

    private boolean enable;

    private Map<String, String> mockUrlMap;

}
