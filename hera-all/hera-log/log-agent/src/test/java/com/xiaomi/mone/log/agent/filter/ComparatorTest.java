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

import com.xiaomi.mone.log.agent.input.AppLogInput;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/3/29 15:11
 */
public class ComparatorTest {

    @Test
    public void test1() {
        AppLogInput newInput = new AppLogInput();
        newInput.setLogPattern("/fsdfdsf/sfsdfs");
        newInput.setLogSplitExpress("/fsfds/sdfsdf");
        AppLogInput oldInput = new AppLogInput();
        oldInput.setLogSplitExpress("/fsfds/sdfsdf");
        Assert.assertEquals(false, newInput.equals(oldInput));
    }

}
