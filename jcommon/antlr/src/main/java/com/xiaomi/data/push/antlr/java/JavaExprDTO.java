/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
