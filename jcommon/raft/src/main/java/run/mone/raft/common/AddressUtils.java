package run.mone.raft.common;

/**
 * @author goodjava@qq.com
 * @date 2022/5/10
 */
public class AddressUtils {


    public static String getRpcAddr(String addr) {
        String[] array = addr.split(":");
        return array[0] + ":" + (Integer.valueOf(array[1]) + 123);
    }

    public static int getRpcPort(int port) {
        return port + 123;
    }

}
