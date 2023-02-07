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

package com.xiaomi.youpin.cat;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 * Propose an alternative to hera
 * CAT监控的集成
 */
@Deprecated
public class CatPlugin {

    private String action;
    private boolean catEnabled;
    private static final String CAT_STATUS_FAILED = "failed";
    private static final String TYPE_REDIS = "redis";
    private String type;

    public CatPlugin(String action, boolean catEnabled) {
        this.action = action;
        this.catEnabled = catEnabled;
        this.type = TYPE_REDIS;
    }

    public CatPlugin(String action, boolean catEnabled, String type) {
        this.action = action;
        this.catEnabled = catEnabled;
        this.type = type;
    }

    /**
     * 创建CAT Transaction, 发数据
     *
     * @param data 　要发给CAT的String 数据
     */
    public void before(String data) {
        if (!isCatEnabled()) {
            return;
        }
    }

    /**
     * 关闭CAT　transaction
     * 如果业务程序失败，设置transaction的status为failed
     *
     * @param success 　transaction的status
     */
    public void after(boolean success) {
        if (!isCatEnabled()) {
            return;
        }
    }

    /**
     * 查看是否启动CAT
     *
     * @return
     */
    private boolean isCatEnabled() {
        return this.catEnabled;
    }

}
