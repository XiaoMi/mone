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

package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.youpin.gwdash.dao.model.ErrorMessage;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.hermes.bo.response.Account;
import lombok.Data;

/**
 * @author zhangjunyi
 * created on 2020/5/20 5:11 下午
 */
@Data
public class OnSiteInspectionItem {
    private  long pipelineId;
    private String deployUser;
    private Project project;
    private ProjectEnv projectEnv;
    private Long deployTime;
    private Boolean deploySucceed;
    private ErrorMessage errorMessage;

}