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

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.sql.SqlTask;
import com.xiaomi.data.push.schedule.task.impl.sql.SqlTaskParam;
import org.junit.Test;

public class SqlTaskTest {


    @Test
    public void testTask() {
        SqlTask sqlTask = new SqlTask();
        TaskParam param = new TaskParam();
        SqlTaskParam sqlTaskParam = new SqlTaskParam();
        sqlTaskParam.setSql("select version()");
        sqlTaskParam.setUsername("shop_x");
        sqlTaskParam.setPasswd("grmtsH4jjSBceLPfLUkmL7eqvmOWGzwq");
        sqlTaskParam.setUrl("jdbc:mysql://xxxx/gateway_web?characterEncoding=utf8&useSSL=false");
        sqlTaskParam.setDriverClassName("com.mysql.jdbc.Driver");
        sqlTaskParam.setParams("");
        sqlTaskParam.setType("query");
        param.put("param", new Gson().toJson(sqlTaskParam));
        TaskContext context = new TaskContext();
        TaskResult res = sqlTask.execute(param, context);
        System.out.println(res.getData());
    }
}
