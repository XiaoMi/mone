package run.mone.ultraman.test;

import cn.hutool.core.codec.Base64;
import com.google.common.net.UrlEscapers;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/6/2 16:58
 */
public class Base64Test {


    @Test
    public void testa() throws InterruptedException {
        Instant begin = new Date().toInstant();
        TimeUnit.SECONDS.sleep(3);
        long sencodes = getSecondsBetween(begin, Instant.now());
        System.out.println(sencodes);
        System.out.println(Math.min(sencodes / 5.0f, 0.99));
        Assert.assertTrue(true);
    }

    public long getSecondsBetween(Instant past, Instant present) {
        return Duration.between(past, present).getSeconds();
    }

    @Test
    public void test() {
        String str = "You must have clicked twice!";
        System.out.println(Base64.encode(str));
    }


    @Test
    public void testDeocde() throws IOException {
        byte[] data = Base64.decode(Files.readAllBytes(Paths.get("/tmp/mp3.txt")));
        Files.write(Paths.get("/tmp/a.mp3"), data);
    }


    @SneakyThrows
    @Test
    public void test1() {
        String str = URLEncoder.encode("你一定点击了两次!", "utf-8");
        System.out.println(str);
    }

    @Test
    public void test3() {
        String str = "@SneakyThrows\n" +
                "    public static void call(String method, String param) {\n" +
                "        param = URLEncoder.encode(param, \"utf8\");\n" +
                "        UltrmanTreeKeyAdapter.browser.getCefBrowser().executeJavaScript(method + \"('\" + param + \"');\", UltrmanTreeKeyAdapter.browser.getCefBrowser().getURL(), 1);\n" +
                "    }";
        str = UrlEscapers.urlFragmentEscaper().escape(str);
        System.out.println(str);
    }

    private List<String> parse(String str1, String str2) {
        str1 = str1.trim();
        str2 = str2.trim();
        int start = 0;
        int end = 0;
        for (; start < str1.length() && start < str2.length(); start++) {
            if (str1.charAt(start) != str2.charAt(start)) {
                break;
            }
        }
        for (; end < str1.length() && end < str2.length(); end++) {
            if (str1.charAt(str1.length() - 1 - end) != str2.charAt(str2.length() - 1 - end)) {
                break;
            }
        }
        List<String> res = new ArrayList<>();
        res.add(str1.substring(start, str1.length() - end).trim());
        res.add(str2.substring(start, str2.length() - end).trim());
        return res;
    }

    @SneakyThrows
    @Test
    public void testText() {
        String str = "abcd1234\n abcd";
        String str2 = "abcd567890abcd";
        System.out.println(parse(str, str2));
    }
}
