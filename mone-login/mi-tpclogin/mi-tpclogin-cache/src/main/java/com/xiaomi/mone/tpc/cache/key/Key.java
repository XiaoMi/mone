package com.xiaomi.mone.tpc.cache.key;

import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.common.util.MD5Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class Key {

    private final int module;
    private final int version;
    private Object[] keys;
    private int time;
    private TimeUnit unit;

    private Key(int module, int version, int time, TimeUnit unit) {
        this.module = module;
        this.version = version;
        this.time = time;
        this.unit = unit;
    }

    public static final Key build(ModuleEnum mdoule) {
        return new Key(mdoule.getCode(), mdoule.getVersion(), mdoule.getTime(), mdoule.getUnit());
    }

    public Key keys(Object... keys) {
        this.keys = keys;
        return this;
    }

    public Key setTime(int time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder keyStr = new StringBuilder();
        keyStr.append("tpc_login_").append(module).append("_").append(version);
        if (keys == null || keys.length <= 0) {
            return keyStr.toString();
        }
        for (Object key : keys) {
            keyStr.append("_").append(key.toString());
        }
        if (keyStr.length() <= 128) {
            return keyStr.toString();
        }
        String md5Key = MD5Util.md5(keyStr.toString());
        log.info("redis key md5 before={}, after={}", keyStr.toString(), md5Key);
        return md5Key;
    }
}
