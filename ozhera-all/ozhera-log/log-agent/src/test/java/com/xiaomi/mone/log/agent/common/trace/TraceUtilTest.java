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
package com.xiaomi.mone.log.agent.common.trace;

import com.xiaomi.hera.tspandata.TSpanData;
import org.junit.Assert;
import org.junit.Test;

public class TraceUtilTest {

    private static String spanStr = "";

    @Test
    public void toTSpanDataTest() {
        TSpanData tSpanData = TraceUtil.toTSpanData(spanStr);
        Assert.assertTrue(tSpanData != null && tSpanData.isSetKind() && tSpanData.isSetStatus()
                && tSpanData.isSetAttributes() && tSpanData.isSetEvents() && tSpanData.isSetLinks()
                && tSpanData.isSetParentSpanContext() && tSpanData.isSetExtra());
    }
}
