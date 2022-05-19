package com.xiaomi.data.push.uds.codes;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class GsonCodes implements ICodes {

    @Override
    public <T> T decode(byte[] data, Type type) {
        return new Gson().fromJson(new String(data), type);
    }

    @Override
    public <T> byte[] encode(T t) {
        return new Gson().toJson(t).getBytes();
    }

    @Override
    public byte type() {
        return 0;
    }
}
