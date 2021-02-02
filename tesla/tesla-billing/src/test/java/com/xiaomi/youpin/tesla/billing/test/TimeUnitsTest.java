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

package com.xiaomi.youpin.tesla.billing.test;

import com.xiaomi.youpin.tesla.billing.common.TimeUtils;
import org.junit.Test;


/**
 * @author goodjava@qq.com
 */
public class TimeUnitsTest {


    @Test
    public void testMonthBegin() {
        System.out.println(TimeUtils.monthBegin(2020, 8));
    }


    @Test
    public void testMonthEnd() {
        System.out.println(TimeUtils.monthEnd(2020, 8));
    }
}
