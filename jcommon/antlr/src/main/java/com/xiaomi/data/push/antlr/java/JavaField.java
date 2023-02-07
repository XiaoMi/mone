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
