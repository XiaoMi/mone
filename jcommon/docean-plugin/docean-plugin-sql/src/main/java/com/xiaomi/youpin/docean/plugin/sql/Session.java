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
import java.util.stream.Collectors;

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


    /**
     * 查询有那些表
     *
     * @return
     */
    public List<String> tables(String schemaName) {
        List<Map<String, ColumnRecord>> list = query("select * from information_schema.TABLES where TABLE_SCHEMA=?", schemaName);
        return list.stream().map(it -> it.get("TABLE_NAME").getData()).collect(Collectors.toList());
    }

    /**
     * 查询表结构
     *
     * @param schemaName
     * @param tableName
     * @return
     */
    public List<Map<String, ColumnRecord>> desc(String schemaName, String tableName) {
        return query("select * from information_schema.columns where TABLE_NAME = ? and TABLE_SCHEMA=?", tableName, schemaName);
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
                ColumnInfo[] columnInfos = getColumnNames(rs);
                List<Map<String, ColumnRecord>> res = Lists.newArrayList();
                while (rs.next()) {
                    Map<String, ColumnRecord> m = Maps.newHashMap();
                    for (ColumnInfo info : columnInfos) {
                        try {
                            ColumnRecord record = new ColumnRecord();
                            record.setName(info.getName());
                            record.setType(info.getType());
                            if (info.getType().equals("BLOB")) {
                                byte[] bytes = rs.getBytes(info.getName());
                                record.setBytes(bytes);
                            } else if (info.getType().equals("DATETIME")) {
                                Timestamp timestamp = rs.getTimestamp(info.getName());
                                if (null != timestamp) {
                                    record.setData(String.valueOf(timestamp.getTime()));
                                }
                            } else {
                                Object val = rs.getObject(info.getName());
                                record.setData(Optional.ofNullable(val).isPresent() ? val.toString() : null);
                            }
                            m.put(info.getName(), record);
                        } catch (SQLException e) {
                            log.error(e.getMessage());
                        }
                    }
                    res.add(m);
                }
                mo.setObj(res);
            } finally {
                safeClose(rs);
                safeClose(preparedStatement);
            }
        }, e ->

        {
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


    private ColumnInfo[] getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        ColumnInfo[] infos = new ColumnInfo[count];
        for (int i = 0; i < count; i++) {
            String name = rsmd.getColumnName(i + 1);
            String type = rsmd.getColumnTypeName(i + 1);
            infos[i] = new ColumnInfo(name, type);
        }
        return infos;
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
