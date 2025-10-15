package run.mone.hive.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.function.InvokeMethodFunction;
import run.mone.hive.mcp.spec.McpSchema;

/**
 * @author goodjava@qq.com
 * @date 2025/10/12 18:32
 */
public class TemplateTest {

    @Test
    public void test1() {
        McpSchema.Implementation i = new McpSchema.Implementation("a","b",ImmutableMap.of("a","1"));
        String str = AiTemplate.renderTemplate("""
                ${v["k"]}
                """, ImmutableMap.of("v",ImmutableMap.of("k","v1")), Lists.newArrayList(Pair.of("invoke", new InvokeMethodFunction())));
        System.out.println(str);
    }

}
