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

package com.xiaomi.youpin;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

public class CommonServiceTest {


    @Test
    public void testList() {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);
        int pageNum = 2;
        int pageSize = 5;

        if (pageSize > list.size()) {
            pageSize = list.size();
        }


        int from = (pageNum - 1) * pageSize;
        if (from >= list.size()) {
            System.out.println(Lists.newArrayList());
        }
        int end = (pageNum) * pageSize;
        if (end > list.size()) {
            end = list.size();
        }
        System.out.println(list.subList(from, end));

        System.out.println(list.size() / pageSize + ((list.size() % pageSize) > 0 ? 1 : 0));
    }
}
