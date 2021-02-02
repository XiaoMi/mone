/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.agent.common;

import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.ks3.KsyunService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class YoupinKsYunService {


    public KsyunService ksyunService;

    private YoupinKsYunService() {
        boolean userFileServer = Boolean.valueOf(Config.ins().get("useFileServer", "false"));
        String fileServerUrl = Config.ins().get("fileServerUrl", "");

        String ksYunId = Nacos.ins().get("ks_yun_id","");
        String ksYunKey = Nacos.ins().get("ks_yun_key","");

        log.info("YoupinKsYunService useFileServer:{} {}, ks_yun_id:{}, ks_yun_key:{}", userFileServer, fileServerUrl, ksYunId, ksYunKey);

        if (userFileServer) {
            ksyunService = new KsyunService(fileServerUrl);
            ksyunService.setToken(Config.ins().get("file_server_token", ""));
        } else {
            ksyunService = new KsyunService();
            ksyunService.setAccessKeyID(ksYunId);
            ksyunService.setAccessKeySecret(ksYunKey);
            ksyunService.init();
        }
    }

    private static class LazyHolder {
        private static YoupinKsYunService ins = new YoupinKsYunService();
    }

    public static YoupinKsYunService ins() {
        return LazyHolder.ins;
    }

}
