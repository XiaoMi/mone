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

package com.xiaomi.data.push.antlr.expr;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhiyong on 07/06/2018.
 */
public class Bean {


    private String p = "abc";

    private int id;
    private String name;

    private List<String> list = Arrays.asList("1","2","3");

    private Map<String,String> map = Maps.newHashMap();

    public Bean(int id, String name) {
        this.id = id;
        this.name = name;
        map.put("a","a");
        map.put("b","b");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String hi(int i) {
        return "hi:" + i + "-------->";
    }

    public String hi(int i, String str) {
        return "hi:" + i + "--" + str;
    }

}
