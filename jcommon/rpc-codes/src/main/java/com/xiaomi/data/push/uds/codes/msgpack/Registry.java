package com.xiaomi.data.push.uds.codes.msgpack;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 09:40
 */
public abstract class Registry<T> {

    public abstract byte[] encode(T obj);

    public abstract T decode(byte[] data);


}
