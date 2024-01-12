package run.mone.mimeter.engine.common;

import java.util.concurrent.ThreadLocalRandom;

public class TraceUtil {
    static String traceString = "0123456789abcdef";

    public static String traceId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] chars = new char[32];
        chars[0] = 'f';
        chars[1] = 'f';
        chars[2] = '8';
        chars[3] = '8';
        for (int i = 4; i < 32; i++) {
            chars[i] = traceString.charAt(random.nextInt(16));
        }
        return new String(chars);
    }

    public static String spanId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] chars = new char[16];
        for (int i = 0; i < 16; i++) {
            chars[i] = traceString.charAt(random.nextInt(16));
        }
        return new String(chars);
    }

}
