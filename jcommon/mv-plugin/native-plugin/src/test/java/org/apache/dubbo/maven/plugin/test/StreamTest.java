package org.apache.dubbo.maven.plugin.test;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/28 14:20
 */
public class StreamTest {


    @Test
    public void testFirst() {
        List<String> l = Lists.newArrayList("a");
        l.stream().findFirst().ifPresent(it-> System.out.println(it));
    }

}
