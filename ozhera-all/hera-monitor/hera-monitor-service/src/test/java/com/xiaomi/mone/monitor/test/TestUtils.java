package com.xiaomi.mone.monitor.test;

import com.xiaomi.mone.monitor.utils.FreeMarkerUtil;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/7/7 8:42 下午
 */
public class TestUtils {


    @Test
    public void testFreeMarker() {

        Map<String, Object> map = new HashMap<>();
        map.put("size", 0);
        map.put("gte_val", "1497283200000");
        map.put("lte_val", "1497928996980");
        map.put("min_val", "1497283200000");
        map.put("max_val", "1497928996980");
        map.put("interval", "21526566ms");

        try {
            //获取工程路径
            String content =  FreeMarkerUtil.getContent("/","eventflow.ftl",map);
            System.out.println("返回的json" + "\n" + content + "\n");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
