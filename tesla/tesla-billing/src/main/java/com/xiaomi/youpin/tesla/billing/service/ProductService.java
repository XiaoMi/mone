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

package com.xiaomi.youpin.tesla.billing.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.billing.common.BillingException;
import com.xiaomi.youpin.tesla.billing.dataobject.Product;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/8/4
 */
@Service
public class ProductService {

    @Resource
    private NutDao dao;

    public Product getProduct(int id) {
        return dao.fetch(Product.class, id);
    }


    public void addProduct(Product product) {
        dao.insert(product);
    }

    public void delProduct(int id) {
        dao.delete(Product.class, id);
    }

    public void updateProduct(Product product) {
        dao.update(product);
    }


    /**
     * 获取产品的价格
     *
     * @param id
     * @return
     */
    public long getPrice(int id) {
        Product product = getProduct(id);
        //包月
        if (product.getType() == 0) {
            //计算出一分钟需要多少钱
            //TODO $--- 需要准确一些
            return product.getPrice() / 30 / 24 / 60;
        }
        //按分钟付费
        if (product.getType() == 1) {
            return product.getPrice();
        }
        throw new BillingException("get price error");
    }

}
