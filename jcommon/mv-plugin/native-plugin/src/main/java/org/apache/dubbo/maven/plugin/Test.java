package org.apache.dubbo.maven.plugin;

import java.util.Set;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/26 14:12
 */
public class Test {

    public static void main(String[] args) {
        ClassFinder finder = new ClassFinder();
        Set<String> set = finder.findClassSet("org.apache.dubbo",msg->{});
        System.out.println(set.size());
    }
}
