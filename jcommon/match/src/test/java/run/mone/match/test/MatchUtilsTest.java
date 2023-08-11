package run.mone.match.test;

import org.junit.Test;
import run.mone.match.MatchUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author goodjava@qq.com
 * @date 2023/8/3 11:10
 */
public class MatchUtilsTest {


    @Test
    public void test1() {
        System.out.println(MatchUtils.match("", ""));
    }

    @Test
    public void testIsMatch() {
        assertTrue(MatchUtils.isMatch("hello", "hello"));
        assertTrue(MatchUtils.isMatch("hello world", "hello\\sworld"));
        assertTrue(MatchUtils.isMatch("12345", "\\d+"));
        assertFalse(MatchUtils.isMatch("hello", "world"));
    }


}
