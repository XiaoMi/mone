package run.mone.rpc.codes.test;

import com.xiaomi.data.push.uds.codes.HessianCodes;
import com.xiaomi.data.push.uds.codes.ProtostuffCodes;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashMap;
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
}
