package run.mone.rpc.codes.test;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.uds.codes.HessianCodes;
import com.xiaomi.data.push.uds.codes.ProtostuffCodes;
import com.xiaomi.data.push.uds.codes.msgpack.MsgpackCodes;
import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/2/2 15:45
 */
public class CodeTest {

    @Test
    public void testCode() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ProtostuffCodes codes = new ProtostuffCodes();
        byte[] data = codes.encode(timestamp);

        Timestamp t2 = codes.decode(data, Timestamp.class);
        System.out.println(t2);
        System.out.println(t2.getTime());
    }


    @Test
    public void testHessian() {
        HessianCodes hessianCodes = new HessianCodes();
        A a = new A();
        a.setId(1);
        a.setName("ttt");
        byte[] data = hessianCodes.encode(a);
        System.out.println("" + hessianCodes.decode(data, A.class));

        Map<String, A> m = new HashMap<>();
        m.put("abc", new A());
        System.out.println("" + hessianCodes.decode(hessianCodes.encode(m), Map.class));
    }

    @Test
    public void testMsgpack() {
        MsgpackCodes codes = new MsgpackCodes();
        List<String> l = Lists.newArrayList("1", "2");
        A a = new A();
        a.setId(1);
        a.setName("ttt");
        byte[] data = codes.encode(123);
        Object l2 = codes.decode(data, int.class);
        System.out.println(l2);
    }

    @Test
    public void testMsgpackObj() {
        MsgpackCodes codes = new MsgpackCodes();
        A a = new A();
        a.setF(12.2f);
        a.setArray(new int[]{7, 7, 7, 7});
        a.setL(123L);
        a.setDou(1112);
        a.setBy((byte) 1);
        a.setId(1);
        a.setName("ttt");
        a.setList(Lists.newArrayList("1", "2", "3"));
        B b = new B();
        b.setTt("tt");
        a.setB(b);
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        a.setMap(m);
        byte[] data = codes.encode(a);
        Object l2 = codes.decode(data, A.class);
        System.out.println(l2);
    }

    @Test
    public void testMsgpackObj2() {
        MsgpackCodes codes = new MsgpackCodes();
        C a = new C();
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        a.setMap(m);
        byte[] data = codes.encode(a);
        Object l2 = codes.decode(data, A.class);
        System.out.println(l2);
    }

    @Test
    public void test4() {
        MsgpackCodes codes = new MsgpackCodes();
        B a = new B();
        a.setTt("ttt");
        byte[] data = codes.encode(a);
        Object l2 = codes.decode(data, A.class);
        System.out.println(l2);
    }

    @Test
    public void test5() {
        MsgpackCodes codes = new MsgpackCodes();
        D a = new D();
        a.setList(Lists.newArrayList("1", "2"));
        byte[] data = codes.encode(a);
        Object l2 = codes.decode(data, D.class);
        System.out.println(l2);
    }

    @Test
    public void test3() {
        C c = new C();
        System.out.println(C.class instanceof Object);
    }

    @Test
    public void test6() {
        MsgpackCodes codes = new MsgpackCodes();
        E a = new E();
        a.setArray(new int[]{1, 2, 3, 4});
        byte[] data = codes.encode(a);
        Object l2 = codes.decode(data, E.class);
        System.out.println(l2);
    }

    @Test
    public void test7() {
        int[] array = new int[]{1, 2, 3};
        Class<? extends int[]> clazz = array.getClass();
        System.out.println(clazz);
        Class<?> clazz2 = clazz.getComponentType();
        System.out.println(clazz2);
    }
}
