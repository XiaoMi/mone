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

package com.xiaomi.data.push.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzhiyong
 * @date 28/05/2018
 */
@Data
public class Health implements Serializable {

    private String version = "0.0.2";

    private String commitDate = "20190404";

    private long qps = 0L;

    public Health(String version, String commitDate, long qps) {
        this.version = version;
        this.commitDate = commitDate;
        this.qps = qps;
    }

    public Health(String version, String commitDate) {
        this.version = version;
        this.commitDate = commitDate;
    }

    public Health() {
    }

    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                ", commitDate='" + commitDate + '\'' +
                ", qps=" + qps +
                '}';
    }
}
