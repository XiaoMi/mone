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

package com.xiaomi.youpin.tesla.plug.test;

import lombok.Data;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@Data
@IocBean
public class Bean {

    private int id;

    private String name;

    @Inject
    private Ioc ioc;

    @Inject
    private Bean2 bean2;

    public Bean() {
    }

    public Bean(String name) {
        this.name = name;
    }

    public String test() {
        return "test:" + name + ":" + ioc + ":" + id + this.bean2;
    }


    public void init() {
        System.out.println("init");
    }

}
