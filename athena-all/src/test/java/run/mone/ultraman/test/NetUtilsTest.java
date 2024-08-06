package run.mone.ultraman.test;

import com.xiaomi.youpin.tesla.ip.util.NetUtils;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @author HawickMason@xiaomi.com
 * @date 6/25/24 2:40 PM
 */
public class NetUtilsTest {

    @Test
    public void testGetLocalAddress() {
        String localAddress = NetUtils.getLocalHost();
        System.out.println("local ip:" + localAddress);
    }
}
