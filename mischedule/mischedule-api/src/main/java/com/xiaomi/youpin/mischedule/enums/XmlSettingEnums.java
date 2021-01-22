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

package com.xiaomi.youpin.mischedule.enums;

public enum XmlSettingEnums {
    NEXUS(0,"https://nexus.d.xiaomi.net/nexus/content/groups/public"),
    PKGS(1,"pkgs.d.xiaomi.net");

    private int code;
    private String repoUrl;

    XmlSettingEnums(int code, String repoUrl) {
        this.code = code;
        this.repoUrl = repoUrl;
    }

    public int getCode() {
        return code;
    }

    public String getRepoUrl() {
        return repoUrl;
    }
}
