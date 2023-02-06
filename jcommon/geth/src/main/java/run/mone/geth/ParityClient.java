package run.mone.geth;

import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

/**
 * @author goodjava@qq.com
 * @date 2022/6/17 16:44
 */
public class ParityClient {

    private static String ip = "http://127.0.0.1:80";

    private ParityClient(){}

    private static class ClientHolder{
        private static final Parity parity = Parity.build(new HttpService(ip));
    }

    public static final  Parity getParity(){
        return ClientHolder.parity;
    }


}
