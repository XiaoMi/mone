package com.xiaomi.data.push.uds.codes;

import com.xiaomi.data.push.uds.codes.protostuff.TimestampDelegate;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.Delegate;
import io.protostuff.runtime.RuntimeEnv;
import io.protostuff.runtime.RuntimeSchema;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/11/22 17:36
 */
public class ProtostuffCodes implements ICodes {

    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    private final static Delegate<Timestamp> TIMESTAMP_DELEGATE =  new TimestampDelegate();

    private final static DefaultIdStrategy idStrategy = ((DefaultIdStrategy) RuntimeEnv.ID_STRATEGY);

    static {
        idStrategy.registerDelegate(TIMESTAMP_DELEGATE);
    }

    @Override
    public <T> T decode(byte[] data, Type type) {
        Schema schema = null;
        schema = getSchema(DataWrapper.class);
        DataWrapper obj = (DataWrapper) schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return (T) obj.getData();
    }

    @Override
    public <T> byte[] encode(T t) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.setData(t);
        Class clazz = wrapper.getClass();
        Schema schema = getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtostuffIOUtil.toByteArray(wrapper, schema, buffer);
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            if (schema != null) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

    @Override
    public byte type() {
        return CodeType.PROTOSTUFF;
    }
}
