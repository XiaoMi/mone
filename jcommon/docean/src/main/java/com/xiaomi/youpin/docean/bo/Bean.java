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

package com.xiaomi.youpin.docean.bo;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Data
public class Bean implements Comparable<Bean> {

    private String name;

    private String alias;

    private Class<?> clazz;

    private Object obj;

    private int type;

    /**
     * 被引用的次数
     */
    private int referenceCnt;

    private Map<String,String> attachments = Maps.newHashMap();

    public void incrReferenceCnt() {
        this.referenceCnt++;
    }

    @Override
    public int compareTo(Bean o) {
        if (this.type < o.getType()) {
            return -1;
        }
        if (this.type > o.getType()) {
            return 1;
        }

        if (this.type == o.type) {
            if (this.referenceCnt < o.referenceCnt) {
                return -1;
            }
            if (this.referenceCnt > o.referenceCnt) {
                return 1;
            }
        }
        return 0;
    }


    public enum Type {
        controller, service, component
    }

}
