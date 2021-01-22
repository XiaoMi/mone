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

package com.xiaomi.youpin.tesla.file.server.test;

import com.xiaomi.youpin.tesla.file.server.service.BaseService;
import com.xiaomi.youpin.tesla.file.server.service.CleanService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CleanTest {

    @Test
    public void testCleanService() {
        CleanService cleanService = new CleanService(BaseService.DATAPATH);
        cleanService.cleanJar("/Users/dingpei/workspace/test1");
        System.out.println("ok");
    }


    @Test
    public void testCreate() {
        File file = new File("/tmp/z/z/y");
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
