package run.mone.ultraman.test;

import com.xiaomi.youpin.tesla.ip.util.XmlUtils;
import org.junit.Test;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/21 11:55
 */
public class XmlUtilsTest {

    @Test
    public void test1() {
        XmlUtils.updateGeneratorConfig("/your path/generatorConfig.xml","Abc");
    }

    @Test
    public void test2() {
        Map<String, String> m = XmlUtils.getMysqlConfigFromMybatisConfig("/your path/generatorConfig.xml");
        System.out.println(m);
    }
}
