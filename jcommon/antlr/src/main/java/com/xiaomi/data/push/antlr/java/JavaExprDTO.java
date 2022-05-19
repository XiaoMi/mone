package com.xiaomi.data.push.antlr.java;

import lombok.Data;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * java源码分析DTO
 *
 * @author shanwenbang@xiaomi.com
 * @date 2021/3/22
 */
@Data
public class JavaExprDTO implements Serializable {
    private List<JavaField> fields = Lists.newArrayList();

    private List<JavaMethod> methods = Lists.newArrayList();

    private String classPackage;

    private ClassInfo classInfo;
}
