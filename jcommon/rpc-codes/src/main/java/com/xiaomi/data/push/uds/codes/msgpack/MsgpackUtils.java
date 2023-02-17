package com.xiaomi.data.push.uds.codes.msgpack;

import lombok.SneakyThrows;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/2/16 14:57
 */
public class MsgpackUtils {


    @SneakyThrows
    private static void pack(Object obj, MessageBufferPacker packer, ExRunnable runnable) {
        if (null == obj) {
            packer.packByte((byte) 0);
        } else {
            packer.packByte((byte) 1);
            runnable.run();
        }
    }


    interface ExRunnable {
        void run() throws Exception;
    }

    interface ExCallable {
        Object call() throws Exception;
    }


    @SneakyThrows
    public static void encode(Object obj, Class clazz, MessageBufferPacker packer) {
        if (clazz.isAssignableFrom(int.class) || clazz.isAssignableFrom(Integer.class)) {
            packer.packString("int");
            pack(obj, packer, () -> packer.packInt((Integer) obj));
            return;
        }

        if (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class)) {
            packer.packString("float");
            pack(obj, packer, () -> packer.packFloat((float) obj));
            return;
        }

        if (clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(Double.class)) {
            packer.packString("double");
            pack(obj, packer, () -> packer.packDouble((double) obj));
            return;
        }

        if (clazz.isAssignableFrom(byte.class) || clazz.isAssignableFrom(Byte.class)) {
            packer.packString("byte");
            pack(obj, packer, () -> packer.packByte((byte) obj));
            return;
        }

        if (clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class)) {
            packer.packString("long");
            pack(obj, packer, () -> packer.packLong((long) obj));
            return;
        }

        if (clazz.equals(String.class)) {
            packer.packString("String");
            pack(obj, packer, () -> packer.packString(obj.toString()));
            return;
        }


        if (Collection.class.isAssignableFrom(clazz)) {
            String clazzName = null != obj ? obj.getClass().getName() : clazz.getName();
            packer.packString("COLLECTION_" + clazzName);
            pack(obj, packer, () -> {
                Collection c = (Collection) obj;
                for (Object v : c) {
                    encode(v, v.getClass(), packer);
                }
            });
            packer.packString("_COLLECTION");
            return;
        }

        if (clazz.isArray()) {
            String clazzName = null != obj ? obj.getClass().getComponentType().getName() : clazz.getComponentType().getName();
            packer.packString("ARRAY_" + clazzName);
            pack(obj, packer, () -> {
                int len = Array.getLength(obj);
                packer.packInt(len);
                for (int i = 0; i < len; i++) {
                    Object v = Array.get(obj, i);
                    encode(v, v.getClass(), packer);
                }
            });
            packer.packString("_ARRAY");
            return;
        }

        if (Map.class.isAssignableFrom(clazz)) {
            String clazzName = null != obj ? obj.getClass().getName() : clazz.getName();
            packer.packString("MAP_" + clazzName);
            pack(obj, packer, () -> {
                Map map = (Map) obj;
                map.entrySet().forEach(e -> {
                    Map.Entry en = (Map.Entry) e;
                    encode(en.getKey(), en.getKey().getClass(), packer);
                    encode(en.getValue(), en.getKey().getClass(), packer);
                });
            });
            packer.packString("_MAP" + clazz.getName());
            return;
        }

        if (clazz instanceof Object) {
            packer.packString("CLASS_" + obj.getClass().getName());
            pack(obj, packer, () -> {
                Field[] fields = obj.getClass().getDeclaredFields();
                Arrays.stream(fields).forEach(it -> {
                    try {
                        it.setAccessible(true);
                        Object v = it.get(obj);
                        encode(v, it.getType(), packer);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            packer.packString("_CLASS");
        }
    }

    @SneakyThrows
    private static Object unpack(MessageUnpacker unpacker, ExCallable callable) {
        byte b = unpacker.unpackByte();
        if (b == 0) {
            return null;
        }
        return callable.call();
    }

    @SneakyThrows
    public static Object decode(MessageUnpacker unpacker) {
        String str = unpacker.unpackString();
        if (str.equals("_CLASS")) {
            return null;
        }
        if (str.equals("_COLLECTION")) {
            return null;
        }

        if (str.equals("_MAP")) {
            return null;
        }

        if (str.startsWith("COLLECTION_")) {
            return unpack(unpacker, () -> {
                String[] ss = str.split("_");
                Collection collection = (Collection) Class.forName(ss[1]).newInstance();
                for (; ; ) {
                    Object v = decode(unpacker);
                    if (null != v) {
                        collection.add(v);
                        continue;
                    }
                    break;
                }
                return collection;
            });
        }

        if (str.startsWith("ARRAY_")) {
            return unpack(unpacker, () -> {
                String[] ss = str.split("_");
                String type = ss[1];
                int len = unpacker.unpackInt();
                Object array = Array.newInstance(getClass(type), len);
                for (int i = 0; ; i++) {
                    Object v = decode(unpacker);
                    if (null != v) {
                        Array.set(array, i, v);
                        continue;
                    }
                    break;
                }
                return array;
            });
        }

        if (str.startsWith("MAP_")) {
            return unpack(unpacker, () -> {
                String[] ss = str.split("_");
                Map map = (Map) Class.forName(ss[1]).newInstance();
                for (; ; ) {
                    Object key = decode(unpacker);
                    if (null == key) {
                        break;
                    }
                    Object value = decode(unpacker);
                    map.put(key, value);
                }
                return map;
            });
        }

        //类
        if (str.startsWith("CLASS_")) {
            return unpack(unpacker, () -> {
                String[] ss = str.split("_");
                Object obj = Class.forName(ss[1]).newInstance();
                Field[] fields = obj.getClass().getDeclaredFields();
                Arrays.stream(fields).forEach(it -> {
                    try {
                        it.setAccessible(true);
                        it.set(obj, decode(unpacker));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
                unpacker.unpackString();
                return obj;
            });
        }


        if (str.equals("int")) {
            return unpack(unpacker, () -> unpacker.unpackInt());
        }

        if (str.equals("String")) {
            return unpack(unpacker, () -> unpacker.unpackString());
        }

        if (str.equals("long")) {
            return unpack(unpacker, () -> unpacker.unpackLong());
        }

        if (str.equals("float")) {
            return unpack(unpacker, () -> unpacker.unpackFloat());
        }

        if (str.equals("double")) {
            return unpack(unpacker, () -> unpacker.unpackDouble());
        }

        if (str.equals("byte")) {
            return unpack(unpacker, () -> unpacker.unpackByte());
        }

        return null;

    }


    @SneakyThrows
    private static Class getClass(String name) {
        if (name.equals("int")) {
            return int.class;
        }
        return Class.forName(name);
    }

}
