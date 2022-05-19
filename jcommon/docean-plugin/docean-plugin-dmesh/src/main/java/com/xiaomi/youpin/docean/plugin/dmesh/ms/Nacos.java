package com.xiaomi.youpin.docean.plugin.dmesh.ms;


import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

/**
 * @author dingpei@xiaomi.com
 * nacos配置操作
 */
@MeshMsService(interfaceClass = Nacos.class, name = "nacos")
public interface Nacos {

    String getConfigStr(String dataId, String group, long timeout);

    boolean publishConfig(String dataId, String group, String content);

    boolean deleteConfig(String dataId, String group);

}
