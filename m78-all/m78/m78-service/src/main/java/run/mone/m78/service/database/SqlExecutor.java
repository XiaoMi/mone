package run.mone.m78.service.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import run.mone.m78.service.bo.database.QueryRunner;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.MD5Utils;
import run.mone.m78.service.dao.entity.ConnectionInfo;
import run.mone.m78.service.dao.entity.QueryContext;
import run.mone.m78.service.dao.entity.TableInfo;
import run.mone.m78.service.dao.mapper.ConnectionInfoMapper;
import run.mone.m78.service.exceptions.QueryException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.DataSourceConstant.DEFAULT_DATASOURCE;

/**
 * @author HawickMason@xiaomi.com
 * @author goodjava@qq.com
 * @date 2/2/24 2:22 PM
 */
@Slf4j
@Service
public class SqlExecutor implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ConnectionInfoMapper connectionInfoMapper;

    private static final Pattern ERROR_MSG_PATTERN = Pattern.compile(".*table\\[.*] must be specified.*");

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    private FlexDataSource flexDataSource;

    // 增大缓存时间到30分钟，兼容查询执行较长时间
    private static Cache<String, QueryRunner> SQL_RUNNER_CACHE =
            Caffeine.newBuilder()
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .evictionListener(
                            (RemovalListener<String, QueryRunner>)
                                    (key, value, cause) -> {
                                        // 缓存失效时，关闭连接池，避免内存泄露
                                        DataSource dataSource = value.getDataSource();
                                        log.info(
                                                "RemovalListener key={}, DataSource={}, cause={}", key, dataSource, cause);
                                        if (dataSource instanceof Closeable) {
                                            try {
                                                ((Closeable) dataSource).close();
                                            } catch (IOException e) {
                                                log.warn("Closeable DataSource failed.", e);
                                            }
                                        }
                                    })
                    .build();

    private static final ScheduledExecutorService DATASOURCE_POOL_EXECUTOR =
            new ScheduledThreadPoolExecutor(
                    20,
                    new BasicThreadFactory.Builder().namingPattern("query-ds-pool-%d").build(),
                    new ThreadPoolExecutor.DiscardPolicy());

    public ResultSetHandler<List<Map<String, Object>>> dataHandler =
            rows -> {
                if (CollectionUtils.isEmpty(rows)) {
                    return new ArrayList<>();
                }
                return rows.stream()
                        .filter(row -> row != null && !row.isEmpty())
                        .map(HashMap::new)
                        .collect(Collectors.toList());
            };

    private QueryRunner initQueryRunner(ConnectionInfo connectionInfo) {
        if (flexDataSource == null) {
            log.error("try to init query runner while context not refreshed, fail-fast!");
            throw new QueryException("Query Runner Init Failed! check log for more info.");
        }

        log.info(
                "-----------数据源host={},port={}",
                connectionInfo.getHost(),
                connectionInfo.getPort());

        QueryRunner queryRunner =
                SQL_RUNNER_CACHE.get(
                        assembleRunnerKey(connectionInfo),
                        key -> {
                            try {
                                // 创建对象
                                HikariDataSource pool = new HikariDataSource();
                                pool.setScheduledExecutor(DATASOURCE_POOL_EXECUTOR);
                                pool.setDriverClassName(DRIVER_CLASS_NAME);
                                pool.setJdbcUrl(connectionInfo.getJdbcUrl());
                                // 查询后会主动关闭连接，所以不创建初始化连接、空闲连接，防止与用户数据源创建无用连接。
                                pool.setMinimumIdle(0);
                                pool.setMaximumPoolSize(100);
                                // 同时发起查询较多，设置较长的等待时间
                                pool.setConnectionTimeout(300000L);
                                pool.setUsername(connectionInfo.getUser());
                                pool.setPassword(connectionInfo.getPwd());
                                pool.setIdleTimeout(30000);
                                pool.setInitializationFailTimeout(20000);
                                pool.setMaxLifetime(60000);
                                pool.setValidationTimeout(2000);
                                flexDataSource.addDataSource(key, pool);
                                return new QueryRunner(key, pool);
                            } catch (Exception e) {
                                throw new QueryException("SQL QUERY RUNNER INIT FAILED!");
                            }
                        });
        if (queryRunner == null) {
            throw new QueryException("SQL QUERY RUNNER INIT FAILED!");
        }
        return queryRunner;
    }

    private String buildErrorMsg(String rowMsg) {
        Matcher matcher = ERROR_MSG_PATTERN.matcher(rowMsg);
        if (matcher.matches()) {
            return "table name must be like [catalog.db.table]";
        } else {
            return rowMsg;
        }
    }


    /**
     * 根据数据源ID和表名获取创建该表的SQL语句。
     */
    public String fetchCreateTableStatement(int datasourceId, String tableName) {
        QueryContext context = new QueryContext();
        context.setConnectionInfo(connectionInfoMapper.selectOneById(datasourceId));
        List<Map<String, Object>> res = exec("show create table " + tableName, context);
        return res.getFirst().get("Create Table").toString();
    }

    /**
     * Stupid OVERLOAD
     */
    public String fetchCreateTableStatement(Long datasourceId, String tableName) {
        QueryContext context = new QueryContext();
        context.setConnectionInfo(connectionInfoMapper.selectOneById(datasourceId));
        List<Map<String, Object>> res = exec("show create table " + tableName, context);
        return res.getFirst().get("Create Table").toString();
    }

    /**
     * 根据数据源ID和表名获取创建该表的SQL语句。
     */
    public String fetchCreateTableStatement(QueryContext context, String tableName) {
        List<Map<String, Object>> res = exec("show create table " + tableName, context);
        return res.getFirst().get("Create Table").toString();
    }


    /**
     * 执行给定的SQL查询，并返回查询结果列表。
     *
     * @param sql     要执行的SQL语句
     * @param context 查询上下文，用于提供查询相关的额外信息
     * @param args    SQL语句中的参数
     * @return 查询结果，每个结果是一个映射，键为列名，值为对应的值
     */
    public List<Map<String, Object>> exec(String sql, QueryContext context, Object... args) {
        return (List<Map<String, Object>>) query(sql, dataHandler, context, args);
    }

    /**
     * 执行批量插入操作
     *
     * @param context 查询上下文，包含连接信息
     * @param tableName 要插入数据的表名
     * @param rows 要插入的行数据列表
     * @return 每个批次插入操作影响的行数数组
     */
	public int[] execBatchInsert(QueryContext context, String tableName, List<Row> rows) {
        ConnectionInfo connectionInfo = context.getConnectionInfo();
        log.info("execBatchInsert connectionInfo = {}", connectionInfo);
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            QueryRunner queryRunner = initQueryRunner(connectionInfo);
            String dataSourceKey = queryRunner.getDataSourceKey();
            DataSourceKey.use(dataSourceKey);
            int[] updated = Db.insertBatch(tableName, rows);
            log.info("execBatchInsert status, affected counts:{}", updated);
            return updated;
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTime();
            log.info("execBatchInsert finish. cost={}", cost);
            DataSourceKey.clear();
        }
    }

    /**
     * 执行批量更新操作
     *
     * @param context 查询上下文，包含连接信息
     * @param tableName 要更新的表名
     * @param rows 要更新的行数据列表
     * @return 受影响的行数
     */
	public int execBatchUpdate(QueryContext context, String tableName, List<Row> rows) {
        ConnectionInfo connectionInfo = context.getConnectionInfo();
        log.info("execBatchUpdate connectionInfo = {}", connectionInfo);
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            QueryRunner queryRunner = initQueryRunner(connectionInfo);
            String dataSourceKey = queryRunner.getDataSourceKey();
            DataSourceKey.use(dataSourceKey);
            int updated = Db.updateBatchById(tableName, rows);
            log.info("execBatchUpdate status, affected counts:{}", updated);
            return updated;
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTime();
            log.info("execBatchUpdate finish. cost={}", cost);
            DataSourceKey.clear();
        }
    }

    /**
     * 批量删除指定表中主键为给定ID列表的记录
     *
     * @param context 查询上下文，包含连接信息
     * @param tableName 要删除记录的表名
     * @param pkName 表的主键字段名
     * @param ids 要删除的记录的主键ID列表
     * @return 删除的记录数
     */
	public int execBatchDelete(QueryContext context, String tableName, String pkName, List<Long> ids) {
        ConnectionInfo connectionInfo = context.getConnectionInfo();
        log.info("execBatchDelete connectionInfo = {}", connectionInfo);
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            QueryRunner queryRunner = initQueryRunner(connectionInfo);
            String dataSourceKey = queryRunner.getDataSourceKey();
            DataSourceKey.use(dataSourceKey);
            int updated = Db.deleteBatchByIds(tableName, pkName, ids);
            log.info("execBatchDelete, affected counts:{}", updated);
            return updated;
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTime();
            log.info("execBatchDelete finish. cost={}", cost);
            DataSourceKey.clear();
        }
    }

    /**
     * 根据数据源ID和用户名获取表信息列表
     *
     * @param datasourceId 数据源ID
     * @param userName     用户名
     * @return 表信息列表
     */
    public List<TableInfo> getTableInfos(Integer datasourceId, String userName) {
        ConnectionInfo connection = connectionInfoMapper.selectOneById(datasourceId);
        return getTableInfosByConnection(connection, userName);
    }

    /**
     * 根据数据库连接信息和用户名获取表信息列表。
     * 如果用户名与连接信息中的用户名不匹配，则返回空列表。
     * 否则，执行SQL查询获取表名，并为每个表创建TableInfo对象，
     * 其中包括表名和主键名。
     *
     * @param connection 数据库连接信息
     * @param userName   用户名
     * @return 包含表信息的列表
     */
    public List<TableInfo> getTableInfosByConnection(ConnectionInfo connection, String userName) {
        if (!userName.equals(connection.getUserName())) {
            return Lists.newArrayList();
        }
        QueryContext context = new QueryContext();
        context.setConnectionInfo(connection);
        List<Map<String, Object>> res = exec("show tables", context);
        return res.stream().map(it -> {
            TableInfo ti = new TableInfo();
            String name = it.values().stream().findAny().get().toString();
            ti.setTableName(name);
            ti.setPkName(getTablePkName(connection, name));
            return ti;
        }).collect(Collectors.toList());
    }

    /**
     * 获取指定表的主键列名
     *
     * @param connectionInfo 数据库连接信息
     * @param tableName 表名
     * @return 主键列名，如果未找到则返回空字符串
     */
	public String getTablePkName(ConnectionInfo connectionInfo, String tableName) {
        String sql = "SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME = '" +
                tableName +
                "' AND CONSTRAINT_NAME = 'PRIMARY';";
        List<Map<String, Object>> res = exec(sql, new QueryContext(connectionInfo));
        if (CollectionUtils.isEmpty(res)) {
            return "";
        }
        Optional<Map<String, Object>> first = res.stream()
                .findFirst();
        if (!first.isPresent()) {
            return "";
        }
        String pkName = first.get().get("COLUMN_NAME").toString();
        log.info("Get pkName:{} of table:{}, under connection of:{}", pkName, tableName, connectionInfo);
        return pkName;
    }

    /**
     * 获取指定数据源ID和表名的字段列表，当前实现返回一个空的ArrayList。
     */
    public List<String> getFields(Integer datasourceId, String tableName) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);

        return new ArrayList<>();
    }


    /**
     * 执行SQL查询并返回结果
     *
     * @param sql 要执行的SQL查询语句
     * @param resultSetHandler 处理查询结果的处理器
     * @param datasourceId 数据源的ID，用于获取连接信息
     * @return 查询结果
     */
	public Object query(String sql, ResultSetHandler resultSetHandler, int datasourceId) {
        ConnectionInfo connectionInfo = connectionInfoMapper.selectOneById(datasourceId);
        QueryContext qc = new QueryContext();
        qc.setConnectionInfo(connectionInfo);
        return query(sql, resultSetHandler, qc);
    }


    public Object query(String sql, ResultSetHandler resultSetHandler, Map<String, String> context) {
        ConnectionInfo connectionInfo = GsonUtils.gson.fromJson(context.get("connectionInfo"), ConnectionInfo.class);
        log.info("query connectionInfo ={}", context.get("connectionInfo"));
        QueryContext qc = new QueryContext();
        qc.setConnectionInfo(connectionInfo);
        return query(sql, resultSetHandler, qc);
    }

    // 查询执行
    public Object query(String sql, ResultSetHandler resultSetHandler, QueryContext context, Object... args) {
        ConnectionInfo connectionInfo = context.getConnectionInfo();
        log.info("query connectionInfo = {}, sql = {}", connectionInfo, sql);
        QueryRunner queryRunner = initQueryRunner(connectionInfo);
        StopWatch stopWatch = StopWatch.createStarted();
        Connection conn = null;
        try {
            conn = queryRunner.getDataSource().getConnection();
            long cost = stopWatch.getTime();
            if (cost > 60_000L) {
                log.info("query getConnection. cost={}, sql={}", cost, sql);
            }
            return queryRunner.query(sql, resultSetHandler, args); // 根据结果类型返回结果
        } catch (SQLException sqlException) {
            String errorMessage = buildErrorMsg(sqlException.getLocalizedMessage());
            log.error("SQL QUERY RUNNER  FAILED! sql={}", sql, sqlException);
            throw new QueryException(errorMessage);
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTime();
            if (cost > 60_000L) {
                log.info("query finish. cost={}, sql={}", cost, sql);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error("SQL QUERY RUNNER CLOSE CONNECTION FAILED!" + e);
                throw new QueryException(e.getMessage());
            }
        }
    }


    /**
     * 执行SQL更新操作
     *
     * @param sql 要执行的SQL语句
     * @param context 查询上下文，包含连接信息
     * @param args SQL语句中的参数
     * @return 受影响的行数
     * @throws QueryException 当SQL执行或连接关闭失败时抛出
     */
	// 查询执行
    public int update(String sql, QueryContext context, Object... args) {
        ConnectionInfo connectionInfo = context.getConnectionInfo();
        log.info("query connectionInfo = {}, sql = {}", connectionInfo, sql);
        QueryRunner queryRunner = initQueryRunner(connectionInfo);
        StopWatch stopWatch = StopWatch.createStarted();
        Connection conn = null;
        try {
            conn = queryRunner.getDataSource().getConnection();
            long cost = stopWatch.getTime();
            if (cost > 60_000L) {
                log.info("update getConnection. cost={}, sql={}", cost, sql);
            }
            return queryRunner.update(sql, args);
        } catch (SQLException sqlException) {
            String errorMessage = buildErrorMsg(sqlException.getLocalizedMessage());
            log.error("SQL UPDATE RUNNER  FAILED! sql={}", sql, sqlException);
            throw new QueryException(errorMessage);
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTime();
            if (cost > 60_000L) {
                log.info("update finish. cost={}, sql={}", cost, sql);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error("SQL UPDATE RUNNER CLOSE CONNECTION FAILED!" + e);
                throw new QueryException(e.getMessage());
            }
        }
    }


    /**
     * m78自身db需要执行非ORM的手工sql时统一使用本方法
     *
     * @param sql
     * @return
     */
    public Object execWithM78MetaBase(String sql) {
        return execWithM78MetaBase(sql, dataHandler);
    }

    public <T> T execWithM78MetaBase(String sql, ResultSetHandler<T> resultSetHandler) {
        try {
            DataSourceKey.use(DEFAULT_DATASOURCE);
            return resultSetHandler.handle(Db.selectListBySql(sql));
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            DataSourceKey.clear();
        }
    }

    /**
     * 执行批量插入操作到指定表
     *
     * @param tableName 表名
     * @param rows 插入的行数据列表
     * @return 每个批次操作影响的行数数组
     */
	public int[] execWithM78MetaBaseBatchInsert(String tableName, List<Row> rows) {
        try {
            DataSourceKey.use(DEFAULT_DATASOURCE);
            int[] updated = Db.insertBatch(tableName, rows);
            log.info("Batch exec status, affected counts:{}", updated);
            return updated;
        } finally {
            DataSourceKey.clear();
        }
    }

    private String assembleRunnerKey(ConnectionInfo connectionInfo) {
        String queryRunnerKey = null;
        try {
            queryRunnerKey =
                    MD5Utils.encrypt(
                            connectionInfo.getJdbcUrl() + connectionInfo.getUser() + connectionInfo.getPwd());
        } catch (Exception e) {
            throw new QueryException("SQL QUERY RUNNER ASSEMBLE RUNNER KEY FAILED!");
        }
        return queryRunnerKey;
    }

    /**
     * 处理应用程序上下文刷新事件
     *
     * @param event 应用程序上下文刷新事件
     */
	@Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.flexDataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        log.info("M78 current datasource:{}", flexDataSource.getDataSourceMap());
    }


}

