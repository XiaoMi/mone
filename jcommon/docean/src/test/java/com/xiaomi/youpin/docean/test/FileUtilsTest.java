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

package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.common.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2022/5/7
 */
public class FileUtilsTest {

    @Test
    public void testFileUtils() {
        System.out.println(FileUtils.home());
        System.out.println(FileUtils.tmp());
    }

    @Test
    public void testForceDelete() throws IOException {
        String fileName = "/home/work/log/dubbo/rpc.log.2022-07-08";
        FileUtils.forceDelete(new File(fileName));
    }

    @Test
    public void testDeleteDirectory() throws IOException {
        String directory = "/home/work/log/zzytest";
        /**
         * Directory must be empty to be deleted
         */
        FileUtils.deleteDirectory(new File(directory));
    }
}

