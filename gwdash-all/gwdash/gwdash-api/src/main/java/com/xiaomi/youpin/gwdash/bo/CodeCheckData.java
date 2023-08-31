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

import lombok.Builder;
import lombok.Data;
import com.xiaomi.youpin.codecheck.po.CheckResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CodeCheckData implements Serializable {

    private long id;

    /**
     * Time consuming
     */
    private long time;

    Map<String, List<CheckResult>> checkRes;
}
