package com.xiaomi.data.push.uds.codes;

import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public interface ICodes {

    <T> T decode(byte[] data, Type type);

    <T> byte[] encode(T t);


    byte type();

}
