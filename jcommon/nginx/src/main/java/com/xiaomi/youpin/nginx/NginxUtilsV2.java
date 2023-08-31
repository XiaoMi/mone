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

package com.xiaomi.youpin.nginx;

import com.github.odiszapc.nginxparser.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class NginxUtilsV2 {

    /**
     * 获取指定upstream名字下的服务器列表
     *
     * @param config
     * @param name
     * @return
     */
    public static List<String> getServers(String config, String name) {
        List<String> list = new LinkedList<>();
        try {
            NgxConfig conf = NgxConfig.read(new ByteArrayInputStream(config.getBytes()));
            List<NgxEntry> v = conf.findAll(NgxConfig.BLOCK, "upstream");
            v.stream().forEach(it -> {
                NgxBlock nb0 = NgxBlock.class.cast(it);
                if (Lists.newArrayList(nb0.getTokens().iterator()).get(1).getToken().equals(name)) {
                    List<NgxEntry> entryParamList = ((NgxBlock) it).findAll(NgxConfig.PARAM, "server");
                    entryParamList.stream().forEach(it2 -> {
                        NgxParam nb = NgxParam.class.cast(it2);
                        list.add(nb.getValue());
                    });
                }
            });
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }


    /**
     * 添加服务器列表到指定upstream下
     *
     * @param config
     * @param name
     * @param serversToAdd
     * @return
     */
    public static String addServer(String config, String name, List<String> serversToAdd) {
        try {
            NgxConfig conf = NgxConfig.read(new ByteArrayInputStream(config.getBytes()));
            List<NgxEntry> v = conf.findAll(NgxConfig.BLOCK, "upstream");
            v.stream().forEach(it -> {
                NgxBlock nb0 = NgxBlock.class.cast(it);
                if (Lists.newArrayList(nb0.getTokens().iterator()).get(1).getToken().equals(name)) {
                    //会清空之前已经有的
                    nb0.getEntries().clear();
                    serversToAdd.stream().forEach(addr -> {
                        NgxParam param = new NgxParam();
                        param.addValue("server " + addr + " max_fails=3 fail_timeout=5s");
                        nb0.addEntry(param);
                    });
                }
            });
            String content = new NgxDumper(conf).dump();
            return content;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


}
