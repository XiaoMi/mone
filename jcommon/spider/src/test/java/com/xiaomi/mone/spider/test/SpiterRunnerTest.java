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

package com.xiaomi.mone.spider.test;

import com.xiaomi.mone.spider.SpiterRunner;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2021/11/3
 * <p>
 * driver 下载路径:https://npm.taobao.org/mirrors/chromedriver
 */
public class SpiterRunnerTest {

    @Test
    public void testRun() throws IOException {
        new SpiterRunner().run();
    }

    @Test
    public void testRun2() {
        new SpiterRunner().run2();
    }

    /**
     * driver 目前放到了tmp目录
     */

    @Test
    public void testWordsFinder() {
        new SpiterRunner().findWords2("正能量文案", 100);
    }
}
