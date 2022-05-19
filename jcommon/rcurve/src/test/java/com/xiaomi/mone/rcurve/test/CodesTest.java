package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.uds.codes.BytesCodes;
import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.codes.HessianCodes;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class CodesTest {


    @Test
    public void testUdsCommand() {
        UdsCommand udsCommand = new UdsCommand();
        udsCommand.setSerializeType((byte) 1);
        udsCommand.putAtt("a", "1");
        udsCommand.putAtt("b", "2");
        udsCommand.setData("abx".getBytes(), false);
        udsCommand.setServiceName("serviceName");
        udsCommand.setMethodName("methodName");
        udsCommand.setParamTypes(new String[]{"int", "String"});
        udsCommand.setParams(new String[]{"1", "abc"});
        udsCommand.setByteParams(new byte[][]{{1, 2, 3}, {4, 5, 6}});
        udsCommand.setCmd("cmd1");
        udsCommand.setMesh(true);
        udsCommand.setMessage("message");
        udsCommand.setCode(500);
        udsCommand.setTimeout(6000);
        udsCommand.setApp("app");
        udsCommand.setRemoteApp("remote app");
        ByteBuf data = udsCommand.encode();
        UdsCommand commnd = new UdsCommand();
        commnd.decode(data);
        System.out.println(commnd.getAttachments());
        System.out.println(new String(commnd.getData()));
        System.out.println(commnd.getServiceName());
        System.out.println(commnd.getMethodName());
        System.out.println(Arrays.toString(commnd.getParamTypes()));
        System.out.println(Arrays.toString(commnd.getParams()));
        System.out.println(Arrays.toString(commnd.getByteParams()));
        System.out.println(commnd.getSerializeType());
        System.out.println(commnd.getCmd());
        System.out.println(commnd.isMesh());
        System.out.println(commnd.getMessage());
        System.out.println(commnd.getCode());
        System.out.println(commnd.getTimeout());
        System.out.println(commnd.getApp());
        System.out.println(commnd.getRemoteApp());
    }


    @Test
    public void testByteBuf() {
        String[] strs = new String[]{"abc", "def"};
        CompositeByteBuf buffer = Unpooled.compositeBuffer(strs.length);

        buffer.addComponents(true, Unpooled.buffer(4).writeInt(strs.length));

        IntStream.range(0, strs.length).forEach(i -> {
            buffer.addComponents(true, Unpooled.buffer(4).writeInt(strs[i].length()));
            buffer.addComponents(true, Unpooled.buffer(strs[i].length()).writeBytes(strs[i].getBytes()));
        });

        int capacity = buffer.capacity();
        byte[] data = new byte[capacity];
        buffer.readBytes(data);
        System.out.println(Arrays.toString(data));
        System.out.println(capacity);
    }

    @Test
    public void testGsonCodes() {
        GsonCodes codes = new GsonCodes();
        Obj obj = new Obj();
        obj.setId(1);
        obj.setName("zzy");
        byte[] data = codes.encode(obj);
        Obj obj2 = codes.decode(data, obj.getClass());
        System.out.println(obj2);
        Assert.notNull(obj2, "null");
    }

    public static <T> Map<String, Object> beanToMap(T bean) {
        return BeanMap.create(bean);
    }


    @Test
    public void testHessianCodes() {
        HessianCodes codes = new HessianCodes();
        Obj obj = new Obj();
        obj.setId(1);
        obj.setName("zzy");
        byte[] data = codes.encode(obj);
        Obj obj2 = codes.decode(data, obj.getClass());
        System.out.println(obj2);
        Assert.notNull(obj2, "null");


        Map<String, Object> m = beanToMap(obj);
        data = codes.encode(m);
        Object m2 = codes.decode(data, Map.class);
        System.out.println(m2);
    }


    @Test
    public void testBytesCodes() {
        BytesCodes codes = new BytesCodes();
        int i = 12;
        byte[] data = codes.encode(i);
        System.out.println((int) codes.decode(data, int.class));

        long l = 12L;
        data = codes.encode(l);
        System.out.println((long) codes.decode(data, long.class));

        String str = "abc";
        data = codes.encode(str);
        System.out.println((String) codes.decode(data, String.class));


        String[] strs = new String[]{"abc", "def", "ddd"};
        data = codes.encode(strs);

        String[] ss = codes.decode(data, String[].class);
        System.out.println(Arrays.toString(ss));


        data = codes.encode(false);

        boolean b = codes.decode(data, boolean.class);
        System.out.println(b);


        byte[][] bytes = new byte[][]{{1, 2, 3}, {4, 5 }};
        data = codes.encode(bytes);
        System.out.println(Arrays.toString(data));


        bytes = codes.decode(data, byte[][].class);
        System.out.println(Arrays.toString(bytes));
    }
}
