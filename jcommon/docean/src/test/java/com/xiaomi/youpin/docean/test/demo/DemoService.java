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

package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Service
@Slf4j
public class DemoService {


    @Resource(shareable = false)
    private DemoDao testDao;


    @Resource(name = "strBean")
    private String str;


    @Resource(lookup = "zzy")
    private DemoA demoA;

    @Resource
    private ErrorReport errorReport;

    public String demoA() {
        return demoA.f();
    }


    public String call() {
        return testDao.get() + "!!!" + str + testDao.toString();
    }


    public String vo() {
        return demoVo().toString();
    }

    @Lookup
    public DemoVo demoVo() {
        return null;
    }

    public void init() {
        log.info("demoService init");
        errorReport.setError(false);
        errorReport.setMessage("error!");
    }

    public void destory() {
        log.info("destory service");
    }
}
