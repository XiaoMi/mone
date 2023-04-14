package run.mone.openai.net;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author goodjava@qq.com
 * @date 2023/4/13 13:50
 */
public class FakeDnsResolver implements DnsResolver {

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return new InetAddress[]{InetAddress.getByAddress(new byte[]{1, 1, 1, 1})};
    }
}
