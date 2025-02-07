/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.service.service.version;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.common.AthenaVersionUtil;
import run.mone.m78.common.Constant;

@Service
@Slf4j

/**
 * VersionService类负责管理和检查不同客户端的版本信息。
 * <p>
 * 该类通过注入的配置值（如IDEA、GOLAND、PYCHARM、VSCODE的版本号）来维护各个客户端的版本信息。
 * 提供了检查客户端版本是否需要升级的功能，并记录相关的警告信息。
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>检查传入的JsonObject是否包含指定的客户端名称和版本，并判断是否需要升级。</li>
 *   <li>根据客户端类型获取对应的版本号。</li>
 *   <li>比较当前版本和最小版本，判断是否需要升级。</li>
 * </ul>
 * <p>
 * 该类使用了Nacos配置中心的注解来自动刷新配置值，并使用了Slf4j记录日志信息。
 */

public class VersionService {

    @NacosValue(value = "${athena.idea.version}", autoRefreshed = true)
    private String athenaIdeaVersion;
    @NacosValue(value = "${athena.golang.version}", autoRefreshed = true)
    private String athenaGolangVersion;
    @NacosValue(value = "${athena.pycharm.version}", autoRefreshed = true)
    private String athenaPycharmVersion;
    @NacosValue(value = "${athena.vscode.version}", autoRefreshed = true)
    private String athenaVscodeVersion;


    /**
     * 检查版本的方法
     * 先判断传入的JsonObject是否包含指定键
     * 获取客户端名称和版本，并判断是否需要升级，若需要则记录警告信息并返回真
     * 否则返回假
     */
    public boolean checkVersion(JsonObject jsonObject) {
        //检查版本
        if (jsonObject.has(Constant.CLIENT_NAME)) {
            String clientName = jsonObject.get(Constant.CLIENT_NAME).getAsString();
            String clientVersion = jsonObject.get(Constant.CLIENT_VERSION).getAsString();

            if (isVersionNeedUpgrades(clientName, clientVersion)) {
                log.warn("client:{}, clientVersion:{} need upgrade", clientName, clientVersion);
                return true;
            }
        }
        return false;
    }

    private boolean isVersionNeedUpgrades(String currentType, String currentVersion) {
        String miniVersion = getVersionByType(currentType);
        if (null == miniVersion) {
            return false;
        }

        return AthenaVersionUtil.compareVersions(currentVersion, miniVersion) < 0;
    }

    /**
     * 根据传进来的type获取对应的版本号，传进来的type需要转大写
     * type为IDEA，取athenaIdeaVersion返回
     * type为GOLAND，取athenaGolangVersion返回
     * type为PYCHARM，取athenaPycharmVersion返回
     * type为VSCODE，取athenaVscodeVersion返回
     */
    private String getVersionByType(String type) {
        switch (type.toUpperCase()) {
            case "IDEA":
                return athenaIdeaVersion;
            case "GOLAND":
                return athenaGolangVersion;
            case "PYCHARM":
                return athenaPycharmVersion;
            case "VSCODE":
                return athenaVscodeVersion;
            default:
                log.error("get version Unknown type: " + type);
                return null;
        }
    }


}
