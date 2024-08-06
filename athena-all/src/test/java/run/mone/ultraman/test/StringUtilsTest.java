package run.mone.ultraman.test;

import com.xiaomi.youpin.tesla.ip.common.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/6/14 10:49
 */
public class StringUtilsTest {


    @Test
    public void test1() {
        String str = StringUtils.extractEndingKeyword("abc(class)");
        System.out.println(str);
    }

    @Test
    public void testExtractEndingKeyword() {
        // Test case 1: Input string contains "(class)"
        String input1 = "abc(class)";
        String result1 = StringUtils.extractEndingKeyword(input1);
        System.out.println(result1);

        // Test case 2: Input string contains "(method)"
        String input2 = "xyz(method)";
        String result2 = StringUtils.extractEndingKeyword(input2);
        System.out.println(result2);

        // Test case 3: Input string contains "(project)"
        String input3 = "test（project）";
        String result3 = StringUtils.extractEndingKeyword(input3);
        System.out.println(result3);
        // Test case 5: Input string does not contain any matching pattern
        String input5 = "noMatch";
        String result5 = StringUtils.extractEndingKeyword(input5);
        System.out.println("result5:" + result5);

        // Test case 6: Input string is aaa(class)bb
        String input6 = "aaa(class)bb";
        String result6 = StringUtils.extractEndingKeyword(input6);
        System.out.println("result6:" + result6);

        // Test case 7: Input string is xx\module
        String input7 = "xx\\module";
        String result7 = StringUtils.extractEndingKeyword(input7);
        System.out.println("result7:" + result7);
    }

}
