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

package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.FlagCal;
import com.xiaomi.data.push.uds.po.Permission;
import org.junit.Test;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/23 15:15
 */
public class FlagCalTest {


    @Test
    public void testCal() {
        FlagCal cal = new FlagCal(0);
        cal.enable(Permission.IS_REQUEST);
        System.out.println(cal.isTrue(Permission.IS_REQUEST));
        System.out.println(cal.getFlag());
    }

}
