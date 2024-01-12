package run.mone.doris;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2024/1/8 10:23
 */
@Slf4j
public class DorisService {

    private HikariDataSource dataSource;

    private Map<String, ConcurrentLinkedQueue<Map<String, Object>>> bufferMap = new ConcurrentHashMap<>();
    private Map<String, List<String>> tableMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorService executorService;

    private Long flushIntervalMillSeconds = 1000L;

    @Setter
    private Integer stream_load_port = 8030;

    private static final String DEFAULT_DRIVER_NAME = "org.mariadb.jdbc.Driver";

    public DorisService(String url, String user, String password) {
        this(DEFAULT_DRIVER_NAME, url, user, password);
    }

    public DorisService(String driver, String url, String user, String password) {
        this.dataSource = getDatasource(driver, url, user, password);

        executorService = Executors.newVirtualThreadPerTaskExecutor();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(this::flush, 1000, flushIntervalMillSeconds, TimeUnit.MILLISECONDS);
    }

    private HikariDataSource getDatasource(String driver, String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(30);
        config.setConnectionTimeout(SECONDS.toMillis(30));
        config.setConnectionTestQuery("SELECT 1");
//        config.setLeakDetectionThreshold(10000); // 设置为30秒

        return new HikariDataSource(config);
    }

    public boolean createTable(String createSql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createSql);

        } // Automatically closes statement
        catch (SQLException e) {
            throw new RuntimeException("createTable error:" + e.getMessage());
        }
        return true;
    }

    public boolean updateTable(String updateSql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(updateSql);

        } // Automatically closes statement
        catch (SQLException e) {
            throw new RuntimeException("updateTable error:" + e.getMessage());
        }
        return true;
    }

    public List<String> getColumnList(String tableName) {
        List<String> columnList = Lists.newArrayList();
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getColumns(null, null, tableName, null)) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    columnList.add(columnName);
                }
            }
        } catch (Exception e) {
            log.error("getColumnList error,tableName:{}", tableName, e);
        }
        return columnList;
    }

    public boolean deleteTable(String deleteSql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(deleteSql);

        } // Automatically closes statement
        catch (SQLException e) {
            throw new RuntimeException("deleteTable error:" + e.getMessage());
        }
        return true;
    }

    private void processBatch(Connection connection, String tableName, List<String> columnList, List<Map<String, Object>> data) throws SQLException {
        String columns = columnList.stream().collect(Collectors.joining(","));
        String placeholders = columnList.stream().map(column -> "?").collect(Collectors.joining(","));
        String insertSql = String.format("INSERT INTO %s (%s) VALUES(%s)", tableName, columns, placeholders);
        connection.setAutoCommit(false);

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            int batchSize = 1000;
            int count = 1;
            for (Map<String, Object> eventLog : data) {
                for (int i = 0; i < columnList.size(); i++) {
                    preparedStatement.setObject(i + 1, eventLog.get(columnList.get(i)));
                }

                preparedStatement.addBatch();
                if (count % batchSize == 0 || count == data.size()) {
                    int[] result = preparedStatement.executeBatch();
                    connection.commit();
                }
                count++;
            }
            log.info("processBatch add end,count:{}", count);
        } catch (SQLException e) {
            connection.rollback();
            log.error("Doris insertSql execute error", e);
        } catch (Exception e) {
            log.error("processBatch exception", e);
        } catch (Throwable e) {
            log.error("processBatch Throwable", e);
        }
    }

    public Boolean send(String tableName, List<String> columnList, Map<String, Object> data) throws Exception {
        if (data == null || data.isEmpty()) {
            return false;
        }
        tableMap.putIfAbsent(tableName, columnList);
        boolean offer = bufferMap.computeIfAbsent(tableName, k -> new ConcurrentLinkedQueue<>()).offer(data);
        log.info("data key:{},data size:{},insert res:{}", tableName, bufferMap.get(tableName).size(), offer);
        return offer;
    }

    public void flush() {
        try {
            for (Map.Entry<String, ConcurrentLinkedQueue<Map<String, Object>>> buffersEntry : bufferMap.entrySet()) {
                if (buffersEntry.getValue().isEmpty()) {
                    continue;
                }
                executorService.submit(() -> {
                    long startTime = System.nanoTime();
                    Connection connection = null;
                    try {
                        connection = dataSource.getConnection();
                        log.info("dataSource Active Connections:{}", dataSource.getHikariPoolMXBean().getActiveConnections());
                        log.info("Threads Awaiting Connection: {}", dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());

                        long endTime = System.nanoTime();

                        long elapsedTimeInMilliseconds = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

                        log.info("get the time it took to connect to the database：" + elapsedTimeInMilliseconds + " 毫秒");
                        List<Map<String, Object>> batch = new ArrayList<>();
                        int bufferBatchSize = 10000;

                        Map<String, Object> data;
                        while ((data = buffersEntry.getValue().poll()) != null) {
                            batch.add(data);

                            if (batch.size() % bufferBatchSize == 0) {
                                processBatch(connection, buffersEntry.getKey(), tableMap.get(buffersEntry.getKey()), batch);
                                batch.clear();
                            }
                        }

                        if (!batch.isEmpty()) {
                            processBatch(connection, buffersEntry.getKey(), tableMap.get(buffersEntry.getKey()), batch);
                        }
                    } catch (Exception e) {
                        log.error("dories flush error", e);
                    } finally {
                        if (null != connection) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                log.error("connection close error", e);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("flush error", e);
        }
    }

    public List<Map<String, Object>> query(String querySql) throws SQLException {
        List<Map<String, Object>> columns = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(querySql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> dataMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    dataMap.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                columns.add(dataMap);
            }
        }
        return columns;
    }
}
