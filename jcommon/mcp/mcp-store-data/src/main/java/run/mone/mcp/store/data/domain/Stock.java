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
package run.mone.mcp.store.data.domain;

import lombok.Data;

@Data
public class Stock {
    private String skuId;           // 商品SKU
    private String productName;    // 产品名称
    private String storeId;        // 门店ID
    private String storeName;      // 门店名称
    private Integer quantity;      // 库存数量
    private String status;         // 库存状态（例如：充足、紧张、无货）
    private String updateTime;     // 库存更新时间
} 