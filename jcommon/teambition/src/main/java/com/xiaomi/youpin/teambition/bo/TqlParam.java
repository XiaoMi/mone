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

package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2021/10/8
 */
@Data
public class TqlParam {
    private String projectId;
    private boolean isArchived;
    private String condition;
    private String userId;
    private Integer priority;
    private Integer isDone;
    private String dueDateStart;
    private String dueDateEnd;
    private String parentId;
}
