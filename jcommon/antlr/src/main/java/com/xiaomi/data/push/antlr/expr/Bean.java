package com.xiaomi.data.push.antlr.expr;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhiyong on 07/06/2018.
 */
public class Bean {


    private String p = "abc";

    private int id;
    private String name;

    private List<String> list = Arrays.asList("1","2","3");

    private Map<String,String> map = Maps.newHashMap();

    public Bean(int id, String name) {
        this.id = id;
        this.name = name;
        map.put("a","a");
        map.put("b","b");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String hi(int i) {
        return "hi:" + i + "-------->";
    }

    public String hi(int i, String str) {
        return "hi:" + i + "--" + str;
    }

}
