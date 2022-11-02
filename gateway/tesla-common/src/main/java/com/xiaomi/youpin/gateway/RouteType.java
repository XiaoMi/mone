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

package com.xiaomi.youpin.gateway;

/**
 * @author goodjava@qq.com
 */
public enum RouteType {
    Http(0, "Http"),
    Dubbo(1, "Dubbo"),
    //分组任务(内部其实是dag任务)
    Group(2, "Group"),
    Plugin(3, "Plugin"),
    //原生dubbo协议
    Native_Dubbo(4, "Native_Dubbo"),
    GRPC(5, "grpc"),
    ;

    private int type;
    private String typeName;

    RouteType(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public int type() {
        return this.type;
    }

    public String typeName() {
        return this.typeName;
    }


    public static Boolean isDubbo(Integer type) {
        return type == RouteType.Dubbo.type;
    }

    public static Boolean isDirect(Integer type) {
        return type == RouteType.Http.type;
    }


    public static RouteType fromType(Integer type) {
        for (RouteType routeType : RouteType.values()) {
            if (routeType.type == type) {
                return routeType;
            }
        }
        return null;
    }


}
