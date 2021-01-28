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

package com.xiaomi.youpin.docean.plugin.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
@Slf4j
public class Session {

    private JdbcTransaction transaction;
    private DataSource dataSource;

    public Session(JdbcTransaction transaction, DataSource dataSource) {
        this.transaction = transaction;
        this.dataSource = dataSource;
        this.transaction.setDataSource(dataSource);
    }


    public List<Map<String, ColumnRecord>> query(String sql, Object... paras) {
        MutableObject mo = new MutableObject();
        Safe.run(() -> {
            ResultSet rs = null;
            PreparedStatement preparedStatement = null;
            try {
                Connection conn = this.transaction.getConnection();
                preparedStatement = conn.prepareStatement(sql);
                for (int i = 0; i < paras.length; i++) {
                    try {
                        preparedStatement.setObject(i + 1, paras[i]);
                    } catch (SQLException e) {
                        log.error(e.getMessage());
                        throw new DoceanException(e);
                    }
                }
                rs = preparedStatement.executeQuery();
                String[] names = getColumnNames(rs);
                List<Map<String, ColumnRecord>> res = Lists.newArrayList();
                while (rs.next()) {
                    Map<String, ColumnRecord> m = Maps.newHashMap();
                    for (String name : names) {
                        try {
                            Object val = rs.getObject(name);
                            ColumnRecord record = new ColumnRecord();
                            record.setName(name);
                            record.setData(Optional.ofNullable(val).isPresent() ? val.toString() : null);
                            record.setType(Optional.ofNullable(val).isPresent() ? val.getClass().getName() : "");
                            m.put(name, record);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    res.add(m);
                }
                mo.setObj(res);
            } finally {
                safeClose(rs);
                safeClose(preparedStatement);
            }
        }, e -> {
            throw new DoceanException(e);
        });
        return (List<Map<String, ColumnRecord>>) mo.getObj();
    }


    private void safeClose(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }


    private String[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String[] name = new String[count];
        for (int i = 0; i < count; i++) {
            name[i] = rsmd.getColumnName(i + 1);
        }
        return name;
    }


    public int update(String sql, Object... paras) {
        MutableObject obj = new MutableObject();
        Safe.run(() -> {
            PreparedStatement preparedStatement = null;
            try {
                Connection conn = this.transaction.getConnection();
                preparedStatement = conn.prepareStatement(sql);
                for (int i = 0; i < paras.length; i++) {
                    try {
                        preparedStatement.setObject(i + 1, paras[i]);
                    } catch (SQLException e) {
                        log.error(e.getMessage());
                        throw new DoceanException(e);
                    }
                }
                int rowsAffected = preparedStatement.executeUpdate();
                obj.setObj(rowsAffected);
            } finally {
                safeClose(preparedStatement);
            }
        }, e -> {
            throw new DoceanException(e);
        });
        return (int) obj.getObj();
    }


    public void commit() {
        Safe.run(() -> this.transaction.commit());
    }

    public void rollback() {
        Safe.run(() -> this.transaction.rollback());
    }

    public void close() {
        Safe.run(() -> this.transaction.close());
    }

}
