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

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author renqingfu
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public abstract class BindUtils {

    private final static String HOME_VOLUME = "xxxx/volume";

    private final static String NACOS_NAMING = "/nacos/naming/public";

    private static Bind log(DockerReq req) {
        CommonUtils.mkdir(req.getLogPath());
        return new Bind(req.getLogPath(), new Volume(req.getLogPath()));
    }

    private static Bind dubboNaming(String name) {
        String dubboVolumeSource = HOME_VOLUME + "/" + name + NACOS_NAMING;
        CommonUtils.mkdir(dubboVolumeSource);
        log.info("bind dubbo naming volume:{}", dubboVolumeSource);
        return new Bind(dubboVolumeSource, new Volume("/root/" + NACOS_NAMING));
    }

    public static List<Bind> getBindList(DockerReq req, String name) {
        List<Bind> bindList = Lists.newArrayList();
        bindList.add(log(req));
        bindList.add(dubboNaming(name));
        customBind(req, bindList);
        return bindList;
    }

    private static void customBind(DockerReq req, List<Bind> bindList) {
        Map<String, String> attachments = req.getAttachments();
        if (null != attachments
                && null != attachments.get("volume")
                && StringUtils.isNotEmpty(attachments.get("volume").trim())) {
            List<String> volumes = new Gson().fromJson(attachments.get("volume"),
                    new TypeToken<List<String>>() {
                    }.getType());
            log.info("volume:{}", volumes);
            for (String it : volumes) {
                String[] files = it.split(":");
                String source = "";
                String target = "";
                if (files.length == 2
                        && StringUtils.isNotEmpty((source = files[0].trim()))
                        && source.startsWith("/")
                        && StringUtils.isNotEmpty((target = files[1].trim()))
                        && target.startsWith("/")) {
                    CommonUtils.mkdir(HOME_VOLUME + source);
                    bindList.add(new Bind(HOME_VOLUME + source, new Volume(target)));
                } else if (files.length == 1
                        && StringUtils.isNotEmpty((source = files[0].trim()))
                        && source.startsWith("/")) {
                    CommonUtils.mkdir(HOME_VOLUME + source);
                    bindList.add(new Bind(HOME_VOLUME + source, new Volume(source)));
                }
            }
        }
    }


}
