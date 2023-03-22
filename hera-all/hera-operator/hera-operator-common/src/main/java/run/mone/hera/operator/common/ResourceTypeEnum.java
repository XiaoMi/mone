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
package run.mone.hera.operator.common;

/**
 * @author shanwb
 * @date 2023-02-10
 */
public enum ResourceTypeEnum {
    /**
     * 集群对外访问service选择，默认只支持：NodePort、LoadBalancer
     */
    SERVICE_CHECK(0, "serviceCheck", 0),

    MYSQL(1, "MySQL", 7),
    REDIS(2, "Redis", 7),
    ES(3, "ES", 7),
    ROCKETMQ(4, "RocketMQ", 7),
    Nacos(5, "Nacos", 8),
    PROMETHEUS(6,"prometheus", 9),

    GRAFANA(7,"grafana", 9),

    ALERT_MANAGER(8,"alertManager", 9),
    CADVISOR(9,"cadvisor", 9),

    NODE_EXPORTER(10,"node-exporter", 9),

    HERA_APP(11, "HeraApp", 10),

    HERA_FE(12, "HeraFe", 11),


    OTHER(99, "Other", 19)
    ;

    private Integer type;

    private String typeName;

    /**
     * 初始化顺序，越小优先级越高
     * 下一优先级 会等 上一优先级全部启动完成 才会开始启动，解决资源前后依赖问题
     */
    private int order;
    private ResourceTypeEnum(int type, String typeName, int order) {
        this.type = type;
        this.typeName = typeName;
        this.order = order;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getOrder() {
        return order;
    }

    public static ResourceTypeEnum typeOf(String typeName) {
        for (ResourceTypeEnum rte : ResourceTypeEnum.values()) {
            if (typeName.equals(rte.getTypeName())) {
                return rte;
            }
        }

        return null;
    }
    
}
