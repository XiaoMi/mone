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

package com.xiaomi.youpin;

import com.xiaomi.youpin.gwdash.common.LockUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LockTest {


    @Test
    public void testLock() {

        MutableInt i = new MutableInt(0);
        MutableInt j = new MutableInt(0);
        LockUtils l = new LockUtils();


        IntStream.range(0, 1000).parallel().mapToObj(it -> {
            j.setValue(j.getValue() + 1);
            return l.lockAndRun("","1ab", 10000, () -> {
                i.setValue(i.getValue() + 1);
                return null;
            });
        }).collect(Collectors.toList());


        System.out.println(i.getValue());
        System.out.println(j.getValue());


    }

}
