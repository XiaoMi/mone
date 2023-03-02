package com.xiaomi.data.push.uds.codes.msgpack;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 09:40
 *
 * 注册那些希望自己序列化和反序列化的类
 */
public abstract class Registry<T> {

    public abstract byte[] encode(T obj);

    public abstract T decode(byte[] data);


}
