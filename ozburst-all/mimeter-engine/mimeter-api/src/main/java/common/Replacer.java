/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package common;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by dongzhenxing on 2023/4/4 1:24 AM
 */
@Data
public class Replacer implements Serializable {
    int taskId;
    int paramIndex;
    String paramName;
    Object value;
    boolean forceStr;

    public Replacer(int taskId, int paramIndex, String paramName, Object value, boolean forceStr) {
        this.taskId = taskId;
        this.paramIndex = paramIndex;
        this.paramName = paramName;
        this.value = value;
        this.forceStr = forceStr;
    }
}
