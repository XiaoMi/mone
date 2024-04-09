package com.xiaomi.data.push.uds.codes;

import com.alibaba.com.caucho.hessian.io.SerializerFactory;

/**
 * @author zhangping17
 * @date 2024/03/09
 */
public class DefaultHessian2FactoryInitializer{

    private static SerializerFactory SERIALIZER_FACTORY;

    public static SerializerFactory getSerializerFactory() {
        if (SERIALIZER_FACTORY != null) {
            return SERIALIZER_FACTORY;
        }
        synchronized (DefaultHessian2FactoryInitializer.class) {
            if (SERIALIZER_FACTORY == null) {
                SERIALIZER_FACTORY = new SerializerFactory();
            }
        }
        return SERIALIZER_FACTORY;
    }
}
