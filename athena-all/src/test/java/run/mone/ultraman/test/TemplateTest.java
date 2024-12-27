package run.mone.ultraman.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.junit.Test;
import run.mone.ultraman.common.TemplateUtils;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/30 13:24
 */
public class TemplateTest {


    @Test
    public void testCustomFunction() {
        String result = TemplateUtils.renderTemplate("${greeting} ${customFunction('World')}", ImmutableMap.of("greeting", "Hello"),
                Lists.newArrayList(Pair.of("customFunction", new Function() {
                    @Override
                    public Object call(Object[] paras, Context ctx) {
                        return "hahahaha:" + paras[0];
                    }
                })));
        System.out.println(result);
    }


    @Test
    public void renderAndPrintTemplate() {
        String str = TemplateUtils.renderTemplate("${name} hi", ImmutableMap.of("name", "zzy"));
        System.out.println(str);
    }

    @Test
    public void testMyFunction() {
        String str = TemplateUtils.renderTemplate("${name} hi ${athena('zzy')}", ImmutableMap.of("name", "zzy"));
        System.out.println(str);
    }


    @Test
    public void printTemplateParams() {
        Map<String, Integer> m = TemplateUtils.getParams("${name} ${age} aaa");
        System.out.println(m);
    }

    @Test
    public void printByteArrayAsString() {
        byte[] data = new byte[]{34, 41, 10};
        String v = (new String(data));
        System.out.println(v);
    }

    class A {
        public String x;
        public String y;
    }

    @Test
    public void testOS() {
        System.out.println(System.getProperty("os.name"));
        A a = new A();
        a.x = "a";
        System.out.println(new Gson().toJson(a));
        System.out.println(new GsonBuilder().serializeNulls().create().toJson(a));
    }

}
