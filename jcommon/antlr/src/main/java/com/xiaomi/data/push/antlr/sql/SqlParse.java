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

package com.xiaomi.data.push.antlr.sql;

import com.xiaomi.data.push.antlr.sql.exceptions.SqlParseException;
import com.xiaomi.data.push.antlr.sql.model.SqlElement;

/**
 * sql解析服务
 * 支持传入批量sql,多条之间用分号区分
 */
public interface SqlParse {

    /**
     * sql解析
     * @param sqlText
     * @return
     * @throws SqlParseException
     */
    SqlElement parse(String sqlText) throws SqlParseException;
}
