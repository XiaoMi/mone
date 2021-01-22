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

package com.xiaomi.youpin.mischedule.api.service;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.api.service.bo.HttpParams;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;

import java.util.List;

public interface AbService {
    public Result<Boolean> abTest(Integer count, String cron, String password, HttpParams httpParams);

    public Result<Boolean> abTestSql(Integer count);

    public Result<Boolean> canAbTest();

    public List<Task> getList();
}
