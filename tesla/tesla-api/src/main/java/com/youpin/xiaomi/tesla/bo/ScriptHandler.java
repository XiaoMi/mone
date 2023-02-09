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

package com.youpin.xiaomi.tesla.bo;

/**
 * @author goodjava@qq.com
 */
public interface ScriptHandler {

    /**
     * 前置处理
     *
     * @param apiInfo
     * @param request
     */
    void before(ApiInfo apiInfo, Object request);


    /**
     * 后置处理
     *
     * @param apiInfo
     * @param request
     * @param response
     * @return
     */
    Object after(ApiInfo apiInfo, Object request, Object response);


}