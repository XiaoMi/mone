package com.xiaomi.youpin.docean.plugin.dmesh.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 16:15
 */
public class CommonTest {


    @Test
    public void testList2() {
        System.out.println(Lists.newArrayList(1,2,3).stream().filter(it->it>22).findAny().orElseThrow(()->{
           return new RuntimeException("ex");
        }));
    }

    @Test
    public void TestGson(){
        Gson gson = new Gson();
        Map<String, Map<String,Double>> tenementSettingMap = new HashMap<>();
        Map<String,Double> map = new HashMap<String, Double>();
        tenementSettingMap.put("youpin", ImmutableMap.of("aa",23.0));


        Map<String,Double> map2 = new HashMap<String, Double>();

        map2.put("1",0.2);

        tenementSettingMap.put("first_dept",map2);

        String s = gson.toJson(tenementSettingMap);
        System.out.println(s);
    }

    @Test
    public void testList() {
        List<String> list = Lists.newArrayList();
        String str = list.stream().map(it->it.toUpperCase()).findFirst().orElse(null);
        System.out.println(str);
    }


    @Test
    public void testLen() {
        byte[]d = new byte[1024];
        byte[] data = new byte[1024 * 1024 * 500];
        System.out.println(d.length);
        System.out.println(data.length);
    }


    @Test
    public void testGetName() {
        System.out.println(new String[]{}.getClass().getName());
    }


    interface IA {

        String a = "abc";

        default String name() {
            return a;
        }
    }


    @Test(expected = Throwable.class)
    public void testInterface() throws NoSuchFieldException, IllegalAccessException {
        IA a = new IA() {

        };

        Field field = a.getClass().getField("a");
        field.setAccessible(true);
        field.set(a,"vvv");

        System.out.println(a.name());
    }
}
