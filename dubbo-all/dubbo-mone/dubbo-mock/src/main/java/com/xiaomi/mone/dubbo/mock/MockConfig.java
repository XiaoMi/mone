package com.xiaomi.mone.dubbo.mock;

import com.xiaomi.mone.dubbo.mock.obj.DubboRefMockInfo;
import com.xiaomi.mone.dubbo.mock.util.ConfigUtils;
import com.xiaomi.mone.dubbo.mock.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MockConfig {

    private static final String MOCK_FILE_NAME = "dubbomock.properties";

    public static final MockConfig INSTANCE = new MockConfig();

    private static final String REQUEST_URL_FORMAT = "%s/%s:%s";

    public static final String MONE_MOCK_ENABLE = "monemock.enable";

    private Boolean enable;

    private Map<String, Boolean> interfaceMockConfig = new HashMap<>();

    private Map<String, DubboRefMockInfo> interfaceMap = new HashMap<>();

    private MockConfig() {
        Properties mockProperties = ConfigUtils.loadProperties(MOCK_FILE_NAME);
        Map<String, DubboRefMockInfo> refMap = new HashMap<>();
        for (String key : mockProperties.stringPropertyNames()) {
            String[] keyArr = key.split("\\.");
            String value = mockProperties.getProperty(key);
            if (MONE_MOCK_ENABLE.equals(key)) {
                enable = "true".equals(value);
            } else {
                if (keyArr.length < 2) {
                    log.warn("DubboMockConfig, invalid property, key: {}, value: {}", key, value);
                    continue;
                }
                String ref = keyArr[1];
                refMap.putIfAbsent(ref, new DubboRefMockInfo());
                DubboRefMockInfo dubboRefMockInfo = refMap.get(ref);
                adapteDubboRefMockInfo(dubboRefMockInfo, key, value);
            }
        }

        interfaceMap = refMap.values().stream().collect(Collectors.toMap(it1 -> it1.getInterfaceName(), it1 -> it1));
    }

    private void adapteDubboRefMockInfo(DubboRefMockInfo dubboRefMockInfo, String key, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        } else if (key.endsWith(".enable")) {
            dubboRefMockInfo.setEnable(Boolean.valueOf(value));
        } else if (key.endsWith(".mock.server.url.prefix")) {
            dubboRefMockInfo.setMockUrlPrefix(value);
        } else if (key.endsWith(".group")) {
            dubboRefMockInfo.setGroup(value);
        } else if (key.endsWith(".version")) {
            dubboRefMockInfo.setVersion(value);
        } else if (key.endsWith(".interface")) {
            dubboRefMockInfo.setInterfaceName(value);
        } else if (key.endsWith(".methods")) {
            dubboRefMockInfo.setMethods(Arrays.asList(value.split(",")));
        } else {
            log.warn("DubboMockConfig.adapteDubboRefMockInfo, invalid property, key: {}, value: {}", key, value);
        }
    }

    public Boolean isMockEnable() {
        return Optional.ofNullable(enable).orElse(false);
    }

    public Boolean isInvocationMockEnable(String interfaceName, String methodName) {
        DubboRefMockInfo dubboRefMockInfo = this.interfaceMap.get(interfaceName);
        if (dubboRefMockInfo == null || !dubboRefMockInfo.isEnable()) {
            return false;
        }
        if (dubboRefMockInfo.getMethods().contains("*") || dubboRefMockInfo.getMethods().contains(methodName)) {
            return true;
        }
        return false;
    }


    public String buildMockRequestUrl(String interfaceName, String methodName) {
        DubboRefMockInfo dubboRefMockInfo = this.interfaceMap.get(interfaceName);
        String md5Location = Md5Utils.getMD5(getServiceKey(dubboRefMockInfo));
        return String.format(REQUEST_URL_FORMAT, dubboRefMockInfo.getMockUrlPrefix(), md5Location, methodName);
    }

    private String getServiceKey(DubboRefMockInfo dubboRefMockInfo) {
        return dubboRefMockInfo.getInterfaceName() + ":" + dubboRefMockInfo.getVersion() + ":" + dubboRefMockInfo.getGroup();
    }

}
