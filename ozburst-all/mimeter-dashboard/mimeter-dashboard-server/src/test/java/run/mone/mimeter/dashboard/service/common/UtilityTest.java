package run.mone.mimeter.dashboard.service.common;

import org.junit.Ignore;
import org.junit.Test;
import run.mone.mimeter.dashboard.common.util.FileUtils;
import run.mone.mimeter.dashboard.common.util.Utility;

import java.util.List;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
@Ignore
public class UtilityTest {

    @Test
    public void testGenId() {
        System.out.println(Utility.generateId(23439L));
    }

    @Test
    public void rehashSalt() {
        System.out.println(Utility.rehashSalt(-23439L, 2));
    }

    @Test
    public void saltVersionedId() {
        System.out.println(Utility.saltVersionedId(99439345L, 999));
    }

    @Test
    public void generateSha256() {
        System.out.println(Utility.generateSha256("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
    }

    @Test
    public void castTest() {
        Object val = 1;
        System.out.println(((Integer)val).longValue());
    }

    @Test
    public void csvTest() {
        String str = "a,\"a,b\",c";
        List<String> lists = FileUtils.parseCsv(str);
        System.out.println(1);
    }
}
