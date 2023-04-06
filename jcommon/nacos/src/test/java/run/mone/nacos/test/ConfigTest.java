package run.mone.nacos.test;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.data.push.nacos.NacosConfig;
import org.junit.Test;
import org.junit.Assert;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/10/19 14:21
 */
public class ConfigTest {

    /**
     * 需要埋入nacos addr env(nacos_addr)
     * example:nacos_addr=127.0.0.1:80
     * @throws NacosException
     */
    @Test
    public void testGetConfig() throws NacosException {
        NacosConfig config = new NacosConfig();
        config.setDataId("zzy_new");
        config.init();
        Map<String, String> map = config.getConfigMap("zzy_new", "DEFAULT_GROUP");
        System.out.println(map);
        System.out.println(config.getConfig("name"));
        Assert.assertNotNull(config.getConfig("name"));
    }
}
