package com.xiaomi.youpin.codecheck.test.container;

import java.util.*;

public class Container {

    public static void main(String... args) {
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>(1);
        list.add("abc");
        Map<String,String> m = new HashMap<>();
        Set<String> s = new HashSet<>(1);
        System.out.println(list.size());
    }

}
