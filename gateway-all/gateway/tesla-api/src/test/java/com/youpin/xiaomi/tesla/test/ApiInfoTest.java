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

package com.youpin.xiaomi.tesla.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import lombok.Data;
import org.junit.Test;

import java.util.Date;

public class ApiInfoTest {

    @Data
    class AI {
        private Date data;
    }


    @Test
    public void testGson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        AI ai = new AI();
        ai.setData(new Date());
        String str = gson.toJson(ai);
        System.out.println(str);
        System.out.println(gson.fromJson(str,AI.class));
    }


//    @Test
//    public void testApiInfo() {
//        ApiInfo ai = new ApiInfo();
//        ai.enable(Flag.ALLOW_CACHE);
//        ai.enable(Flag.ALLOW_MOCK);
//        System.out.println(ai.getFlag());
//
//
//        System.out.println(ai.isAllow(Flag.ALLOW_CACHE));
//        System.out.println(ai.isAllow(Flag.ALLOW_MOCK));
//        System.out.println(ai.isAllow(Flag.ALLOW_LOG));
//    }
}
