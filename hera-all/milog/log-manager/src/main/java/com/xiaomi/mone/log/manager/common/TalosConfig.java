package com.xiaomi.mone.log.manager.common;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.manager.model.TalosConfigModel;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/8 17:25
 */
@Configuration
public class TalosConfig {

    private String talosAccessKey = Config.ins().get("talos_access_key", "");
    private String talosAccessSecret = Config.ins().get("talos_access_secret", "");
    private String talosAccessClusterInfo = Config.ins().get("talos_access_clusterInfo", "");
    private String talosAccessTopic = Config.ins().get("talos_access_topic", "");

    @Bean
    public TalosConfigModel talosConfigModel() {
        TalosConfigModel talosConfigModel = new TalosConfigModel();
        talosConfigModel.setTalosAccessKey(talosAccessKey);
        talosConfigModel.setTalosAccessSecret(talosAccessSecret);
        talosConfigModel.setTalosAccessClusterInfo(talosAccessClusterInfo);
        talosConfigModel.setTalosAccessTopic(talosAccessTopic);
        return talosConfigModel;
    }


}
