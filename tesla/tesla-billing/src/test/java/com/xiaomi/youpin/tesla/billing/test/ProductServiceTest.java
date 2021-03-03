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

package com.xiaomi.youpin.tesla.billing.test;


import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.billing.dataobject.Product;
import com.xiaomi.youpin.tesla.billing.service.ProductService;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/8/5
 */
public class ProductServiceTest extends BaseTest {


    @Test
    public void testAdd() {
        ProductService service = Ioc.ins().getBean(ProductService.class);
        Product product = new Product();
        product.setName("ali machine");
        product.setPrice(1000L);
        product.setType(0);
        service.addProduct(product);

        //按分钟计费
        Product product2 = new Product();
        product2.setName("ali machine minute");
        product2.setPrice(10L);
        product2.setType(1);
        service.addProduct(product2);

    }
}
