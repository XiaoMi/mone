package com.xiaomi.youpin.docean.plugin.dmesh.ms.orm;

import lombok.SneakyThrows;

import javax.sql.rowset.serial.SerialBlob;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/1 14:17
 */
public abstract class OrmInterceptor {

    protected Object value(byte[] data, Type type) {
        if (type.equals(Blob.class)) {
            if (null == data) {
                return null;
            }
            Blob serialBlob = getBlob(data);
            return serialBlob;
        }
        if (type.equals(byte[].class)) {
            if (null == data) {
                return null;
            }
            return data;
        }
        return null;
    }

    protected Object value(String v, Type type) {
        if (type.equals(Date.class)) {
            if (null == v) {
                return null;
            }
            return new Date(Long.valueOf(v));
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            if (null == v) {
                return 0;
            }
            return Integer.valueOf(v);
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            if (null == v) {
                return 0L;
            }
            return Long.valueOf(v);
        }


        if (type.equals(Timestamp.class)) {
            if (null == v) {
                return null;
            }
            Timestamp timestamp = new Timestamp(Long.valueOf(v));
            return timestamp;
        }

        if (type.equals(String.class)) {
            return v;
        }
        return null;
    }

    @SneakyThrows
    private SerialBlob getBlob(byte[] data) {
        return new SerialBlob(data);
    }


    @SneakyThrows
    protected Object obj(Class clazz) {
        Object ins = clazz.newInstance();
        return ins;
    }

    @SneakyThrows
    protected void setProperty(Object obj, String property, Object value) {
        if (null != value) {
            Field field = obj.getClass().getDeclaredField(property);
            field.setAccessible(true);
            field.set(obj, value);
        }
    }


}
