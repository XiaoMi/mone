/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EsClusterTest {

    private EsCluster esCluster;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        esCluster = Ioc.ins().getBean(EsCluster.class);
    }

    @Test
    public void getByRegion() {
        Assert.assertNull(esCluster.getByRegion(""));
        Assert.assertNull(esCluster.getByRegion(null));
        System.out.println(esCluster.getByRegion(Constant.ES_REGION_EROUP));
    }
}