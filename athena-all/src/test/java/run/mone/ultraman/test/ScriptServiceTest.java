package run.mone.ultraman.test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.ip.service.ScriptService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2023/7/4 14:13
 */
@Slf4j
public class ScriptServiceTest {


    @SneakyThrows
    @Test
    public void test1() {
        String content = getScript();
        Object res = ScriptService.ins().invoke(content, "sum", Maps.newHashMap(), 1, 2);
        System.out.println(res);
    }

    @NotNull
    private static String getScript() throws IOException {
        String content = Resources.toString(Resources.getResource("fun.groovy"), Charsets.UTF_8);
        return content;
    }


    @SneakyThrows
    @Test
    public void test2() {
        Object res = ScriptService.ins().invoke(getScript(), "k", ImmutableMap.of("v", 22));
        System.out.println(res);
    }


    @SneakyThrows
    @Test
    public void test3() {
        Object res = ScriptService.ins().invoke(getScript(), "g", ImmutableMap.of("gson", new Gson(), "log", log), new Gson(),log);
        System.out.println(res);
    }

}
