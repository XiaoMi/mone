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

package com.xiaomi.youpin.mischedule.task.test;

import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.shell.ShellTask;
import org.junit.Test;

public class ShellTaskTest {


    @Test
    public void testTask() {
        ShellTask task = new ShellTask();
        TaskParam param = new TaskParam();
        param.put("path", "/tmp/");
        param.put("cmd", "ls");
        TaskContext context = new TaskContext();
        TaskResult res = task.execute(param, context);
        System.out.println(res.getData());
    }


    @Test
    public void testBuildGolang() {
        ShellTask task = new ShellTask();
        TaskParam param = new TaskParam();
        param.put("path", "/Users/zhangzhiyong/go/workspace/src/test.com/zzy-test/");
        param.put("cmd", "go build");
        TaskContext context = new TaskContext();
        TaskResult res = task.execute(param, context);
        System.out.println(res.getData());
    }
}
