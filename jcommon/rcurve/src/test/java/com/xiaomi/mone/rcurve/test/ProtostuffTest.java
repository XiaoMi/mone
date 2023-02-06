package com.xiaomi.mone.rcurve.test;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.uds.codes.ProtostuffCodes;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/11/22 17:38
 */
public class ProtostuffTest {

    @Test
    public void testProtostuff() {
        ProtostuffCodes codes = new ProtostuffCodes();
        RuntimeException re = new RuntimeException("error");
        byte[] data = codes.encode(re);
        RuntimeException re2 = codes.decode(data, RuntimeException.class);
        System.out.println(re2.getMessage());

        Data data1 = new Data(123);
        byte[] byte1 = codes.encode(data1);
        data1 = codes.decode(byte1, Data.class);
        System.out.println(data1);

        int v = codes.decode(codes.encode(123), int.class);
        System.out.println(v);

        String vv = codes.decode(codes.encode("abc"), String.class);
        System.out.println(vv);


        List<String> vvl = codes.decode(codes.encode(Lists.newArrayList("a", "b")), List.class);
        System.out.println(vvl);

        V o = new V();
        o.setData("123");
        V<String> vvla = codes.decode(codes.encode(o), V.class);
        System.out.println(vvla);

        Map<String, Data> m = new HashMap<>();
        Data d = new Data(1);
        m.put("a", d);
        Map<String, Data> vvm = codes.decode(codes.encode(m), HashMap.class);
        System.out.println(vvm);


        Class vvv = codes.decode(codes.encode(Data.class), Class.class);
        System.out.println(vvv);

    }
}
