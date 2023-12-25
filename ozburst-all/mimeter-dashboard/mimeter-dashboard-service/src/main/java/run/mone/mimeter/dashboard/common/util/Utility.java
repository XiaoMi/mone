package run.mone.mimeter.dashboard.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import run.mone.mimeter.dashboard.pojo.common.Pageable;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
public class Utility {

    public static void handlePagination(Pageable example, Integer pageSize, Integer pageNo) {
        if (pageSize == null) {
            return;
        }
        example.setLimit(pageSize);

        if (pageNo != null) {
            checkArgument(pageNo >= 1, "invalid pageNo " + pageNo);
            example.setOffset((pageNo - 1L) * pageSize);
        }
    }

    public static String generateId() {
        return generateId(0L);
    }

    public static String generateId(Long salt) {
        long ts = System.currentTimeMillis();
        long base10 = 0;
        long prefix = salt == null ? 0L : rehashSalt(salt, 2);
        long multiplier = 1;

        for (int i = 0; i < 11; i++) {
            base10 += (ts % 10) * multiplier;
            multiplier *= 10;
            ts /= 10;
        }
        base10 += prefix * multiplier;
        return Long.toString(base10, 36).toUpperCase();
    }

    public static int rehashSalt(Long salt, int bits) {
        if (salt == null || salt == 0 || bits <= 0) {
            return 0;
        }
        if (salt < 0) {
            salt = -salt;
        }
        int result = 0;

        while (salt > 0) {
            long val = 0;
            int multiplier = 1;

            for (int i = 0; i < bits && salt > 0; i++) {
                val += (salt % 10) * multiplier;
                salt /= 10;
                multiplier *= 10;
            }
            result += val;
        }
        return result % 100;
    }

    public static String saltVersionedId(long salt, int version) {
        checkArgument(version >= 0 && salt >= 0, "invalid version or salt");
        String str = String.format("%08d%02d%08d", rehashSalt(salt, 8), version,
                rehashSalt(System.currentTimeMillis() / 1000, 8));
        return Long.toString(Long.parseLong(str), 36).toUpperCase();
    }

    public static String generateSha256(String originalString) {
        return DigestUtils.sha256Hex(originalString);
    }

    public static Map<String, Integer> convertMapType(Map<String, Long> map1) {
        Map<String, Integer> dict = new HashMap<>();

        map1.forEach((k, v) -> {
            if (k != null && v != null) {
                dict.put(k, v.intValue());
            }
        });
        return dict;
    }
}
