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

package com.xiaomi.youpin.gateway.group;


import com.xiaomi.youpin.gateway.group.filter.DefaultFilter;
import com.xiaomi.youpin.gateway.group.filter.GateWayFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
public class GroupInfo {

    private List<String> methods = new ArrayList<>();
    private GateWayFilter filter = new DefaultFilter();

    /**
     * 下一个分组需要执行的,也就是下一阶段
     */
    private String nextGroup;


    public GroupInfo() {
    }


    public GroupInfo(List<String> methods, GateWayFilter filter, String nextGroup) {
        this.methods = methods;
        this.filter = filter;
        this.nextGroup = nextGroup;
    }


    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public GateWayFilter getFilter() {
        return filter;
    }

    public void setFilter(GateWayFilter filter) {
        this.filter = filter;
    }

    public String getNextGroup() {
        return nextGroup;
    }

    public void setNextGroup(String nextGroup) {
        this.nextGroup = nextGroup;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "methods=" + methods +
                ", filter=" + filter +
                ", nextGroup='" + nextGroup + '\'' +
                '}';
    }
}
