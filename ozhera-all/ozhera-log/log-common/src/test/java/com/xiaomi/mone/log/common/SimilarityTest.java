/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.common;

import cn.hutool.core.date.DateUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/16 12:48
 */
public class SimilarityTest {

    @Test
    public void test1() {
        String content1 = "log_info.log";
        String content2 = "log-debug.2023443";
        String content3 = "log-debug.log";
        String content4 = "log_warn.log";
        System.out.println(FuzzySearch.ratio(content2, content1));
        System.out.println(FuzzySearch.ratio(content2, content3));
        System.out.println(FuzzySearch.ratio(content2, content4));
    }

    @Test
    public void test2(){
        System.out.println(DateUtil.parse("2022/01/23 23:23:34").getTime());
    }
}
