package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.common.ClassFinder;
import org.junit.Test;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/22
 */
public class ClassFinderTest {

    @Test
    public void testFindClassSet() {
        Set<String> res = new ClassFinder().findClassSet("com.google");
        res.stream().forEach(System.out::println);
    }
}
