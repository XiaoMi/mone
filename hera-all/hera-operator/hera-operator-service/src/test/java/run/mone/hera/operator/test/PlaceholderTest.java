package run.mone.hera.operator.test;

import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/6/14 15:56
 */
public class PlaceholderTest {

    @Test
    public void testReplace() {
        Map<String, String> params = new HashMap<>();
        params.put("nacos.address", "nacos:80");
        params.put("es.address", "es:9200");

        String properties = "dubbo.registry.address=nacos://${nacos.address}\n" +
                            "nacos.config.addrs=${nacos.address}\n" +
                            "nacos.address=${nacos.address}\n" +
                            "es.trace.address=${es.address}\n" +
                            "es.trace.username=elastic\n" +
                            "es.trace.password=elastic";

        StringSubstitutor sub = new StringSubstitutor(params);
        String content= sub.replace(properties);
        System.out.println(content);
    }


}
