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
