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
