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

import com.google.gson.Gson

//前置执行
def before(apiInfo, req) {
//    print(apiInfo, req)
    //可以修改入参
    apiInfo.id = 11111
    System.out.println(apiInfo)
    log.info("----------->test log")
}


//后置执行
def after(apiInfo, req, res) {
    //可以修改返回结果
    gson = new Gson()
    Map m = gson.fromJson(res, Map.class)
    m.put("msg", m.get("msg") + " modify")
    gson.toJson(m)
}


