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

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.common.HttpResult;
import com.xiaomi.youpin.gwdash.common.HttpUtils;
import com.xiaomi.youpin.gwdash.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CatController {

    @Value("${cat.base.url}")
    private String catBaseUrl;

    @RequestMapping(value = "/api/cat/list", method = RequestMethod.GET)
    public Result<HttpResult> catListTest() {
        HttpResult result = HttpUtils.get("http://" + catBaseUrl + "/cat/r/t?ip=All&queryname=&domain=tesla&type=tesla&export=true",
                null,
                null,
                100000);

        return Result.success(result);
    }

}
