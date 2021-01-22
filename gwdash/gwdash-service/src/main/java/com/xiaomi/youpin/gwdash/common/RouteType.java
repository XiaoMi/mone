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

package com.xiaomi.youpin.gwdash.common;

/**
 * @author goodjava@qq.com
 */
public enum RouteType {
    Http(0, "Http"),
    Dubbo(1, "Dubbo"),
    ApiCompose(2, "ApiCompose"),
    Plugin(3, "Plugin"),
    OriginalDubbo(4, "OriginalDubbo");

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

    public static Boolean isValidRouteType(Integer type) {
        for (RouteType routeType : RouteType.values()) {
            if (routeType.type == type) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isDubbo(Integer type) {
        return type == RouteType.Dubbo.type || type == RouteType.OriginalDubbo.type;
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
