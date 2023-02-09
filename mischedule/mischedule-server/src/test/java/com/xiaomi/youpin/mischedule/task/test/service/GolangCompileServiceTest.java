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

package com.xiaomi.youpin.mischedule.task.test.service;

import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.cloudcompile.CloudCompileService;
import com.xiaomi.youpin.mischedule.cloudcompile.GolangCompileService;
import com.xiaomi.youpin.mischedule.task.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GolangCompileServiceTest extends BaseTest {


    @Autowired
    private CloudCompileService golangCompileService;

    @Test
    public void testCompile() {
        CompileParam request = new CompileParam();
        request.setGitUrl("");
        request.setBranch("d988e0705b4c558965be83103603e819853f73af");
        request.setProfile("staging");
        request.setTags("Compile");
        request.setGitName("");
        request.setGitToken("");
        request.setJarPath("-server");
        request.setRepoType(0);
        request.setId(2428L);
        golangCompileService.compile(request);
    }
}
