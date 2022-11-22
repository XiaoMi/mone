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

package com.youpin.xiaomi.tesla.plugin.bo;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class DubboDatasource extends TeslaDatasource {

    /**
     * 服务名称
     */
    private String appName;

    /**
     * 注册服务器地址
     */
    private String regAddress;

    /**
     * api包路径
     */
    private String apiPackage;

    /**
     * 线程数设置
      */
    private int threads;


    public DubboDatasource() {
        this.setType(DsType.dubbo.name());
    }

    public DubboDatasource copy() {
        DubboDatasource d = new DubboDatasource();
        d.setRegAddress(this.getRegAddress());
        d.setAppName(this.getAppName());
        d.setType(this.getType());
        d.setName(this.getName());
        d.setApiPackage(this.apiPackage);
        d.setThreads(this.threads);
        return d;
    }
}
