package run.mone.m78.service.database;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.service.bo.database.FieldCmtBO;
import run.mone.m78.service.bo.database.MetaBO;
import run.mone.m78.service.bo.database.TableBO;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.*;
import static run.mone.m78.api.constant.TableConstant.*;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 6:10 PM
 */
@Slf4j
@Service
public class MySQLTablePersist implements TablePersist {

    public static String ID_NAME = "m78_id";

    private static final List<String> NO_DEFAULT_VALUE_TYPES = Arrays.asList("text", "blob", "json", "geometry", "TEXT", "BLOB", "JSON", "GEOMETRY");

    @Resource
    private SqlExecutor sqlExecutor;

    /**
     * 根据给定的SQL语句创建表
     *
     * @param createSql 创建表的SQL语句
     * @param force 是否强制创建表
     * @return 创建的表信息
     * @throws RuntimeException 如果SQL语句中的表名为空
     */
	@Override
    public TableBO createTableBySql(String createSql, boolean force) {
        // TODO: respond force
        String tableName = SqlParseUtil.getTableName(createSql);
        log.info("create table : {}, sql: {}", tableName, createSql);
        if (StringUtils.isBlank(tableName)) {
            throw new RuntimeException("创建表的sql表名为空");
        }
        sqlExecutor.execWithM78MetaBase(createSql);
        return TableBO.builder()
                .tableName(tableName)
                .status(CommonConstant.SUCCESS)
                .build();
    }

    /**
     * 根据元数据创建表
     *
     * @param meta 元数据对象
     * @param force 是否强制创建
     * @return 创建的表对象
     */
	@Override
    public TableBO createTableByMeta(MetaBO meta, boolean force) {
        // TODO
        return null;
    }


    /**
     * Inserts a new row into the specified table with the provided values, returning a TableBO object indicating the operation's status. If the values map is empty, logs a warning and returns a failed status. Constructs a dynamic SQL INSERT statement based on the table name and values, executes it, and logs the operation.
     */
    @Override
    public TableBO insert2Table(String tableName, Map<String, Object> values) {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            log.info("insert into table:{}, size: {}", tableName, values.size());
            log.debug("inserting data: {}", values);
            if (MapUtils.isEmpty(values)) {
                log.warn("insert to table: {}, but values all not valid!", tableName);
                return TableBO.builder()
                        .tableName(tableName)
                        .status(CommonConstant.FAILED)
                        .build();
            }
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" insert into " + tableName + " ( ");
            Set<Map.Entry<String, Object>> entries = values.entrySet();
            int cnt = 0;
            for (Map.Entry<String, Object> entry : entries) {
                sqlBuilder.append(entry.getKey());
                if (cnt != entries.size() - 1) {
                    sqlBuilder.append(",");
                }
                cnt++;
            }
            sqlBuilder.append(" ) ");
            sqlBuilder.append(" values ( ");
            int cnt1 = 0;
            for (Map.Entry<String, Object> entry : entries) {
                Object value = entry.getValue();
                if (value instanceof String) {
                    sqlBuilder.append("'");
                    sqlBuilder.append(value);
                    sqlBuilder.append("'");
                } else {
                    sqlBuilder.append(value);
                }
                if (cnt1 != entries.size() - 1) {
                    sqlBuilder.append(",");
                }
                cnt1++;
            }
            sqlBuilder.append(" ) ");
            sqlBuilder.append(" ; ");
            String sql = sqlBuilder.toString();
            log.info("insert to table: {} , sql : {}", tableName, sql);
            sqlExecutor.execWithM78MetaBase(sql);
            return TableBO.builder()
                    .tableName(tableName)
                    .status(CommonConstant.SUCCESS)
                    .build();
        } finally {
            log.info("insert one row to table:{}, time cost:{}ms", tableName, sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * 批量插入数据到指定表
     *
     * @param tableName 表名
     * @param values 插入的数据列表，每个元素是一个包含列名和对应值的Map
     * @return 包含表名和插入状态的TableBO对象
     */
	// 批量插入表名为tableName的表
    @Override
    public TableBO insert2TableBatch(String tableName, List<Map<String, Object>> values) {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            log.info("Batch insert into table: {}", tableName);
            log.debug("inserting data:{}", values);
            if (CollectionUtils.isEmpty(values)) {
                log.warn("Batch insert to table: {}, but values list is empty or null!", tableName);
                return TableBO.builder().tableName(tableName).status(CommonConstant.FAILED).build();
            }

//            Set<String> sample = values.get(0).keySet();
//            List<String> sampleHeaders = sample.stream()
//                    .filter(h -> !h.equals(ID_NAME))
//                    .map(h -> "`" + h + "`")
//                    .collect(Collectors.toList());
//            String columns = String.join(", ", sampleHeaders);
//            String valuePlaceholders = sampleHeaders.stream()
//                    .map(column -> "?")
//                    .collect(Collectors.joining(", "));

//            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + valuePlaceholders + ")";
//            log.info("Batch insert SQL: {}", sql);
//
//            List<Object[]> batchArgs = new ArrayList<>();
//            for (Map<String, Object> valueMap : values) {
//                batchArgs.add(valueMap.values().toArray());
//            }

            List<Row> rows = values.stream().map(data -> {
                Row row = new Row();
                data.forEach(row::set);
                row.setPrimaryKeys(Sets.newHashSet(RowKey.of(ID_NAME)));
                return row;
            }).collect(Collectors.toList());
            try {
                int[] updateCounts = sqlExecutor.execWithM78MetaBaseBatchInsert(tableName, rows);
                int totalInserted = Arrays.stream(updateCounts).sum();
                log.info("Batch insert into table: {} completed, total rows affected: {}", tableName, totalInserted);
                return TableBO.builder().tableName(tableName).status(CommonConstant.SUCCESS).build();
            } catch (Exception e) {
                log.error("Batch insert into table: {} failed, error: {}", tableName, e.getMessage());
                return TableBO.builder().tableName(tableName).status(CommonConstant.FAILED).build();
            }
        } finally {
            log.info("batch insert into table:{}, time cost:{} s", tableName, sw.elapsed(TimeUnit.SECONDS));
        }
    }

    /**
     * 根据给定的插入SQL语句将数据插入到表中，并返回包含表名和状态的TableBO对象
     *
     * @param sql 插入数据的SQL语句
     * @return 包含表名和操作状态的TableBO对象
     */
	@Override
    public TableBO insert2TableByInsertSql(String sql) {
        String tableName = SqlParseUtil.getTableName(sql);
        log.info("insert to table: {}, sql: {}", tableName, sql);
        sqlExecutor.execWithM78MetaBase(sql);
        return TableBO.builder()
                .tableName(tableName)
                .status(CommonConstant.SUCCESS)
                .build();
    }

    /**
     * 执行SQL查询并返回结果封装为TableBO对象
     *
     * @param sql 要执行的SQL查询语句
     * @return 包含查询结果的TableBO对象
     */
	@Override
    public TableBO query(String sql) {
        String tableName = SqlParseUtil.getTableName(sql);
        log.info("executing query table:{}, sql: {}", tableName, sql);
        List<Map<String, Object>> res = sqlExecutor.execWithM78MetaBase(sql, sqlExecutor.dataHandler);
        log.info("sql:{} size:{}", sql, res.size());
        return TableBO.builder()
                .tableName(tableName)
                .status(CommonConstant.SUCCESS)
                .data(res)
                .build();
    }


    /**
     * Retrieves the SQL CREATE TABLE statement for a given table name using JDBC, throws IllegalArgumentException if the table name is blank, and handles any DataAccessExceptions.
     */
    public String getCreateTableStatement(String tableName) {
        log.info("Retrieving create table statement for: {}", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("Table name must not be blank");
        }
        String showCreateTableQuery = "SHOW CREATE TABLE " + tableName;
        try {
            List<Map<String, Object>> result = sqlExecutor.execWithM78MetaBase("show create table " + tableName, sqlExecutor.dataHandler);
            return result != null ? (String)  result.get(0).get("Create Table") : null;
        } catch (DataAccessException e) {
            log.error("Error retrieving create table statement for table: {}", tableName, e);
            return null;
        }
    }

    /**
     * 修改某表中某个字段的注释值
     *
     * @param tableName 表名
     * @param columnName 字段名
     * @param comment 新的注释内容
     * @throws IllegalArgumentException 如果表名或字段名为空
     * @throws RuntimeException 如果字段不存在或获取字段创建语句时发生错误
     */
	// 修改某表中某个字段的comment值
    public void updateColumnComment(String tableName, String columnName, String comment) {
        log.info("Updating comment for column: {} in table: {}", columnName, tableName);
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("Table name and column name must not be blank");
        }

        try {
            String sql = "SHOW FULL COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
            List<Map<String, Object>> list = sqlExecutor.execWithM78MetaBase(sql, sqlExecutor.dataHandler);
            Map<String, Object> result = list.get(0);
            if (result != null && result.containsKey("Type")) {
                String columnType = (String) result.get("Type");
                String nullable = "YES".equals(result.get("Null")) ? "NULL" : "NOT NULL";
                String key = (result.containsKey("Key") && "PRI".equals(result.get("Key"))) ? "PRIMARY KEY" : "";
                String defaultValue = "NOT NULL".equals(nullable) && result.containsKey("Default") && result.get("Default") != null && !NO_DEFAULT_VALUE_TYPES.contains(columnType)
                        ? "DEFAULT '" + result.get("Default") + "'"
                        : "";
                String extra = result.containsKey("Extra") ? result.get("Extra").toString() : "";
                String updateCommentSql = "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + columnType + " " + nullable + " " + key + " " + defaultValue + " " + extra + " COMMENT '" + comment + "';";
                sqlExecutor.execWithM78MetaBase(updateCommentSql);
                log.info("Updated comment for column: {} in table: {}, updateSql: {}", columnName, tableName, updateCommentSql);

            } else {
                throw new RuntimeException("Column: " + columnName + " does not exist in table: " + tableName);
            }
        } catch (DataAccessException e) {
            log.error("Error retrieving create statement for column: {} in table: {}", columnName, tableName, e);
            throw new RuntimeException("Error retrieving column create statement", e);
        }
    }

    /**
     * 获取某表中字段与注释的映射
     *
     * @param tableName 表名
     * @return 字段与注释的映射列表，如果发生异常则返回空列表
     * @throws IllegalArgumentException 如果表名为空
     */
	// 获取某表中字段与comment的映射，入参为表名tableName, 返回一个Map(project)
    @Override
    public List<FieldCmtBO> getFieldCommentMapping(String tableName) {
        log.info("get field comment mapping from table: {}", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("Table name must not be blank");
        }
        try {
            String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT FROM information_schema.COLUMNS  WHERE TABLE_SCHEMA = '"
                    + META_SCHEMA
                    + "' AND TABLE_NAME = '"
                    + tableName
                    + "' ORDER BY ORDINAL_POSITION";
            List<FieldCmtBO> res = sqlExecutor.execWithM78MetaBase(sql, rows ->
                    rows.stream()
                            .map(
                                    r -> FieldCmtBO.builder()
                                            .fieldName(r.getString("COLUMN_NAME"))
                                            .comment(r.getString("COLUMN_COMMENT"))
                                            .build()
                            )
                            .collect(Collectors.toList())
            );
            return res;
        } catch (Exception e) {
            log.error("Error while try to get field comment mapping from table: {}, nested exception is: ", tableName);
            return new ArrayList<>();
        }
    }

    /**
     * 检查指定的表是否存在
     *
     * @param tableName 表名
     * @return 如果表存在返回true，否则返回false
     * @throws IllegalArgumentException 如果表名为空或仅包含空白字符
     */
	@Override
    public boolean checkExistTable(String tableName) {
        log.info("check if exist table: {}", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("Table name must not be blank");
        }
        try {
            String checkSql = "SELECT 1 FROM information_schema.tables WHERE table_schema = '"
                    + META_SCHEMA
                    + "' AND table_name = '"
                    + tableName
                    + "'";
            List<Map<String, Object>> list = sqlExecutor.execWithM78MetaBase(checkSql, sqlExecutor.dataHandler);
            Map<String, Object> result = list.get(0);
            return MapUtils.isNotEmpty(result) && result.containsKey("1");
        } catch (Exception e) {
            log.error("Error while try to check table: {} 's existence, nested exception is: ", tableName, e);
            return false;
        }
    }

    /**
     * 根据表名获取表头信息
     *
     * @param tableName 表名
     * @return 表头信息列表，如果发生异常则返回空列表
     * @throws IllegalArgumentException 如果表名为空或仅包含空白字符
     */
	@Override
    public List<String> getTableHeaderByTableName(String tableName) {
        log.info("get table header by tableName: {}", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("Table name must not be blank");
        }
        try {
            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"
                    + tableName
                    + "' ORDER BY ORDINAL_POSITION";
            List<Map<String, Object>> res = sqlExecutor.execWithM78MetaBase(sql, sqlExecutor.dataHandler);
            return res.stream()
                    .map(m -> (String) m.get(COLUMN_NAME))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while try get table header by tableName:{} , nested exception is: ", tableName);
            return Lists.newArrayList();
        }
    }




}
