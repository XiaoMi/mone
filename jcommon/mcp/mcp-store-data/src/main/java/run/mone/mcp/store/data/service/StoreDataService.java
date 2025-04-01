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
package run.mone.mcp.store.data.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import run.mone.mcp.store.data.domain.Sku;
import run.mone.mcp.store.data.domain.Stock;

@Service
public class StoreDataService {

    public List<Sku> getSku(String productName){
        return mock();
    }

    private List<Sku> mock(){
        // 第一个 SKU - 小米15 Pro 钛金黑 1TB版本
        Sku sku = new Sku();
        sku.setSkuId("M15P-TI-1TB");
        sku.setProductName("小米15 Pro");

        Sku.Args args = new Sku.Args();
        args.setColor("钛金黑");
        args.setPrice("6999");
        args.setModel("尊享版");
        args.setRam("16GB");
        args.setStorage("1TB");
        args.setCpu("骁龙8 Gen 3");
        sku.setArgs(args);

        // 第二个 SKU - 小米15 星河白 512GB版本
        Sku sku2 = new Sku();
        sku2.setSkuId("M15-WH-512GB");
        sku2.setProductName("小米15");

        Sku.Args args2 = new Sku.Args();
        args2.setColor("星河白");
        args2.setPrice("4999");
        args2.setModel("标准版");
        args2.setRam("12GB");
        args2.setStorage("512GB");
        args2.setCpu("骁龙8 Gen 3");
        sku2.setArgs(args2);

        // 第三个 SKU - 小米15 翡翠青 256GB版本
        Sku sku3 = new Sku();
        sku3.setSkuId("M15-GR-256GB");
        sku3.setProductName("小米15");

        Sku.Args args3 = new Sku.Args();
        args3.setColor("翡翠青");
        args3.setPrice("5499");
        args3.setModel("特别版");
        args3.setRam("12GB");
        args3.setStorage("256GB");
        args3.setCpu("骁龙8 Gen 3");
        sku3.setArgs(args3);

        return Arrays.asList(sku, sku2, sku3);
    }

    public Stock getStock(String sku, String storeId) {
        Map<String, Stock> mockData = mockStockMap();
        String key = sku + "_" + storeId;
        return mockData.getOrDefault(key, createEmptyStock(sku, storeId));
    }

    private Map<String, Stock> mockStockMap() {
        Map<String, Stock> stockMap = new HashMap<>();
        
        // 小米15 Pro 钛金黑 1TB版本的库存
        Stock stock1 = new Stock();
        stock1.setSkuId("M15P-TI-1TB");
        stock1.setProductName("小米15 Pro");
        stock1.setStoreId("SH001");
        stock1.setStoreName("上海南京东路店");
        stock1.setQuantity(50);
        stock1.setStatus("充足");
        stock1.setUpdateTime("2024-03-20 10:00:00");
        stockMap.put("M15P-TI-1TB_SH001", stock1);

        Stock stock2 = new Stock();
        stock2.setSkuId("M15P-TI-1TB");
        stock2.setProductName("小米15 Pro");
        stock2.setStoreId("SH002");
        stock2.setStoreName("上海徐家汇店");
        stock2.setQuantity(5);
        stock2.setStatus("紧张");
        stock2.setUpdateTime("2024-03-20 10:00:00");
        stockMap.put("M15P-TI-1TB_SH002", stock2);

        // 小米15 星河白 512GB版本的库存
        Stock stock3 = new Stock();
        stock3.setSkuId("M15-WH-512GB");
        stock3.setProductName("小米15");
        stock3.setStoreId("SH001");
        stock3.setStoreName("上海南京东路店");
        stock3.setQuantity(100);
        stock3.setStatus("充足");
        stock3.setUpdateTime("2024-03-20 10:00:00");
        stockMap.put("M15-WH-512GB_SH001", stock3);

        return stockMap;
    }

    private Stock createEmptyStock(String sku, String storeId) {
        Stock stock = new Stock();
        stock.setSkuId(sku);
        stock.setStoreId(storeId);
        stock.setQuantity(0);
        stock.setStatus("无货");
        stock.setUpdateTime("2024-03-20 10:00:00");
        return stock;
    }
}
