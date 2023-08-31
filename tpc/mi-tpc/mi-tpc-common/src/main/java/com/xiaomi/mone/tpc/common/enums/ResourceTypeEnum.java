package com.xiaomi.mone.tpc.common.enums;

import com.xiaomi.mone.tpc.common.param.*;
import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ResourceTypeEnum implements Base {
    ADD_MYSQL(0, "mysql", AddMysqlParam.class),
    ADD_PLUGIN(1, "plugin", AddPluginParam.class),
    ADD_DUBBO(2, "dubbo", AddDubboParam.class),
    ADD_REDIS(3, "redis", AddRedisParam.class),
    ADD_NACOS(4, "nacos", AddNacosParam.class),
    ADD_MONGO(5, "mongodb", AddMongoParam.class),
    ADD_ES(6,"es", AddEsParam.class),
    ADD_ROCKETMQ(7,"rocketmq",AddRocketMqParam.class),
    ADD_ZK(8,"zookeeper",AddZKParam.class),
    ADD_QUOTA(9,"quota",AddQuotaParam.class),
    ADD_K8S(10,"k8s-token", AddK8sTokenParam.class),
    ADD_SERVERLESS(11,"serverless",AddServerlessParam.class),
    ;
    private Integer code;
    private String desc;
    private Class<? extends ArgCheck> clazz;
    ResourceTypeEnum(Integer mode, String desc, Class<? extends ArgCheck> clazz) {
        this.code = mode;
        this.desc = desc;
        this.clazz = clazz;
    }

    public static final ResourceTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResourceTypeEnum userTypeEnum : ResourceTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public static final ResourceTypeEnum getEnumByString(String type) {
        if (type == null) {
            return null;
        }
        for (ResourceTypeEnum userTypeEnum : ResourceTypeEnum.values()) {
            if (type.equals(userTypeEnum.desc)) {
                return userTypeEnum;
            }
        }
        return null;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public Class<? extends ArgCheck> getClazz() {
        return clazz;
    }

}
