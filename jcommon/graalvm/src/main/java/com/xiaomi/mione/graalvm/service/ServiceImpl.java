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

package com.xiaomi.mione.graalvm.service;

import com.xiaomi.mione.graalvm.anno.LogAnno;
import com.xiaomi.youpin.docean.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 6/4/21
 */
@Service
public class ServiceImpl implements IService,MService,ZService,DestoryService {

    @LogAnno
    @Override
    public String hi() {
        return "hi";
    }

    @Override
    public String version() {
        return "0.0.1";
    }

    @Override
    public String destory() {
        return "destory";
    }

    @Override
    public String z() {
        return "z";
    }

    @Override
    public void $destory() {
        System.out.println("destory");
    }
}
