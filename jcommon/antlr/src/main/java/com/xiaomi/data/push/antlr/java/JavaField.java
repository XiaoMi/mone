package com.xiaomi.data.push.antlr.java;

import lombok.Data;

import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/9 17:06
 */
@Data
public class JavaField {

    private String modifier;

    private String name;

    private boolean fstatic;

    private boolean ffinal;

    private String data;

    private List<String> annos;


}
