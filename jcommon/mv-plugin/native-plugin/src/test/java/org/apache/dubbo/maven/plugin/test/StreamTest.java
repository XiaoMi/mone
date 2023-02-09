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

package org.apache.dubbo.maven.plugin.test;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/28 14:20
 */
public class StreamTest {


    @Test
    public void testFirst() {
        List<String> l = Lists.newArrayList("a");
        l.stream().findFirst().ifPresent(it-> System.out.println(it));
    }

}
