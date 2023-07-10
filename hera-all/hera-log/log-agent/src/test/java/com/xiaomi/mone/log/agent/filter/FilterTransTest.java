/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.filter;

import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import junit.framework.TestCase;

import java.util.HashMap;

public class FilterTransTest extends TestCase {

    public void testFilterConfTrans() {
        FilterDefine filterDefine = new FilterDefine();
        filterDefine.setCode(Common.RATE_LIMIT_CODE + 0);
        filterDefine.setArgs(new HashMap<String, String>() {{
            put(Common.PERMITS_PER_SECOND, "100");
        }});
        FilterConf filterConf = FilterTrans.filterConfTrans(filterDefine);
        System.out.println(filterConf);
    }
}