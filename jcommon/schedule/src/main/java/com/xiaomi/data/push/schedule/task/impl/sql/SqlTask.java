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

package com.xiaomi.data.push.schedule.task.impl.sql;

import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhiyong on 09/06/2018.
 * <p>
 * 可以提交sql(包括查询和更新)
 */
@Component
public class SqlTask extends AbstractTask {

    @Task(name = "sqlTask")
    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        SqlTaskParam sqlTaskParam = new Gson().fromJson(param.get("param"), SqlTaskParam.class);
        String sql = sqlTaskParam.getSql();
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        try {
            dataSource.setDriverClass((Class<? extends Driver>) Class.forName(sqlTaskParam.getDriverClassName()));

            dataSource.setUsername(sqlTaskParam.getUsername());
            dataSource.setPassword(sqlTaskParam.getPasswd());
            dataSource.setUrl(sqlTaskParam.getUrl());

            JdbcTemplate jt = new JdbcTemplate(dataSource);

            String params = sqlTaskParam.getParams();
            //执行的类型
            String sqlType = sqlTaskParam.getType();
            String[] ss = params.split(",");

            TaskResult result = TaskResult.Success();

            //更新
            if ("update".equals(sqlType)) {
                int num = jt.update(sql, ss);
                result.setData(String.valueOf(num));
            } else {//查询
                List<Map<String, Object>> list = params.length() > 0 ? jt.queryForList(sql, ss) : jt.queryForList(sql);
                result.setData(new Gson().toJson(list));
            }
            return result;
        } catch (Exception e) {
            return TaskResult.Failure(e.getMessage());
        }
    }
}
