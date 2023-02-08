package run.mone.rpc.codes.test;

import com.xiaomi.data.push.uds.codes.ProtostuffCodes;
import org.junit.Test;

import java.sql.Timestamp;

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
}
