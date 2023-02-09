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

import com.xiaomi.youpin.gwdash.common.GwCache;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 */
public class GwCacheTest {


    @Test
    public void testCache() {
        GwCache cache = new GwCache();
        cache.init();
        cache.put(GwCache.HOUR,"name","zzy");
        System.out.println(cache.get(GwCache.HOUR,"name"));
    }
}
