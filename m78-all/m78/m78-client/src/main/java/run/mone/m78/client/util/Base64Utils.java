package run.mone.m78.client.util;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 14:34
 */
public class Base64Utils {

    public static String decodeBase64String(String str) {
        return new String(Base64.getDecoder().decode(str.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"));
    }
}
