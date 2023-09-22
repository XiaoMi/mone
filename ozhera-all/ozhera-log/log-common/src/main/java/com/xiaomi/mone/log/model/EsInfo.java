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
package com.xiaomi.mone.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EsInfo {
    private Long id;
    /**
     * ES address
     */
    private String addr;

    /**
     * ES user name
     */
    private String user;

    /**
     * ES password
     */
    private String pwd;

    private String token;

    private String catalog;

    private String database;

    public EsInfo(Long id, String addr, String user, String pwd) {
        this.id = id;
        this.addr = addr;
        this.user = user;
        this.pwd = pwd;
    }

    public EsInfo(Long id, String addr, String token, String catalog, String database) {
        this.id = id;
        this.addr = addr;
        this.token = token;
        this.catalog = catalog;
        this.database = database;
    }
}
