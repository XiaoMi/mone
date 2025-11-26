package run.mone.hive.memory.longterm.storage;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 历史记录管理器
 * 使用SQLite存储记忆的变更历史
 */
@Slf4j
public class HistoryManager {
    
    private final String dbPath;
    private Connection connection;
    
    // SQL语句
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            memory_id TEXT NOT NULL,
            prev_value TEXT,
            new_value TEXT,
            event TEXT NOT NULL,
            created_at TEXT,
            updated_at TEXT,
            actor_id TEXT,
            role TEXT,
            is_deleted INTEGER DEFAULT 0,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
        """;
    
    private static final String INSERT_HISTORY_SQL = """
        INSERT INTO history (memory_id, prev_value, new_value, event, created_at, updated_at, actor_id, role, is_deleted)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    
    private static final String GET_HISTORY_SQL = """
        SELECT * FROM history WHERE memory_id = ? ORDER BY timestamp DESC
        """;
    
    private static final String DELETE_HISTORY_SQL = """
        DELETE FROM history WHERE memory_id = ?
        """;
    
    public HistoryManager(String dbPath) {
        this.dbPath = dbPath;
        initializeDatabase();
    }
    
    /**
     * 初始化数据库连接和表结构
     */
    private void initializeDatabase() {
        try {
            // 创建数据库目录
            java.nio.file.Path dbFilePath = java.nio.file.Paths.get(dbPath);
            java.nio.file.Files.createDirectories(dbFilePath.getParent());
            
            // 连接数据库
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.setAutoCommit(true);
            
            // 创建表
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(CREATE_TABLE_SQL);
            }
            
            log.info("History database initialized at: {}", dbPath);
            
        } catch (Exception e) {
            log.error("Failed to initialize history database", e);
            throw new RuntimeException("Failed to initialize history database", e);
        }
    }
    
    /**
     * 添加历史记录
     * 
     * @param memoryId 记忆ID
     * @param prevValue 之前的值
     * @param newValue 新值
     * @param event 事件类型 (ADD/UPDATE/DELETE)
     * @param createdAt 创建时间
     * @param actorId 操作者ID
     * @param role 角色
     */
    public void addHistory(String memoryId, String prevValue, String newValue, String event,
                          String createdAt, String actorId, String role) {
        addHistory(memoryId, prevValue, newValue, event, createdAt, null, actorId, role);
    }
    
    /**
     * 添加历史记录（完整版本）
     * 
     * @param memoryId 记忆ID
     * @param prevValue 之前的值
     * @param newValue 新值
     * @param event 事件类型
     * @param createdAt 创建时间
     * @param updatedAt 更新时间
     * @param actorId 操作者ID
     * @param role 角色
     */
    public void addHistory(String memoryId, String prevValue, String newValue, String event,
                          String createdAt, String updatedAt, String actorId, String role) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_HISTORY_SQL)) {
            pstmt.setString(1, memoryId);
            pstmt.setString(2, prevValue);
            pstmt.setString(3, newValue);
            pstmt.setString(4, event);
            pstmt.setString(5, createdAt);
            pstmt.setString(6, updatedAt);
            pstmt.setString(7, actorId);
            pstmt.setString(8, role);
            pstmt.setInt(9, "DELETE".equals(event) ? 1 : 0);
            
            pstmt.executeUpdate();
            
            log.debug("Added history record for memory: {}, event: {}", memoryId, event);
            
        } catch (Exception e) {
            log.error("Failed to add history record for memory: {}", memoryId, e);
            throw new RuntimeException("Failed to add history record", e);
        }
    }
    
    /**
     * 获取记忆的历史记录
     * 
     * @param memoryId 记忆ID
     * @return 历史记录列表
     */
    public List<Map<String, Object>> getHistory(String memoryId) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(GET_HISTORY_SQL)) {
            pstmt.setString(1, memoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("id", rs.getLong("id"));
                    record.put("memory_id", rs.getString("memory_id"));
                    record.put("prev_value", rs.getString("prev_value"));
                    record.put("new_value", rs.getString("new_value"));
                    record.put("event", rs.getString("event"));
                    record.put("created_at", rs.getString("created_at"));
                    record.put("updated_at", rs.getString("updated_at"));
                    record.put("actor_id", rs.getString("actor_id"));
                    record.put("role", rs.getString("role"));
                    record.put("is_deleted", rs.getInt("is_deleted"));
                    record.put("timestamp", rs.getString("timestamp"));
                    
                    history.add(record);
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to get history for memory: {}", memoryId, e);
            throw new RuntimeException("Failed to get history", e);
        }
        
        return history;
    }
    
    /**
     * 删除记忆的历史记录
     * 
     * @param memoryId 记忆ID
     */
    public void deleteHistory(String memoryId) {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_HISTORY_SQL)) {
            pstmt.setString(1, memoryId);
            
            int deleted = pstmt.executeUpdate();
            log.debug("Deleted {} history records for memory: {}", deleted, memoryId);
            
        } catch (Exception e) {
            log.error("Failed to delete history for memory: {}", memoryId, e);
            throw new RuntimeException("Failed to delete history", e);
        }
    }
    
    /**
     * 获取所有历史记录数量
     * 
     * @return 历史记录总数
     */
    public long getHistoryCount() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM history")) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (Exception e) {
            log.error("Failed to get history count", e);
        }
        
        return 0;
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总记录数
            stats.put("total_records", getHistoryCount());
            
            // 按事件类型统计
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT event, COUNT(*) as count FROM history GROUP BY event")) {
                
                Map<String, Long> eventCounts = new HashMap<>();
                while (rs.next()) {
                    eventCounts.put(rs.getString("event"), rs.getLong("count"));
                }
                stats.put("event_counts", eventCounts);
            }
            
            // 最近的记录时间
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(timestamp) as latest FROM history")) {
                
                if (rs.next()) {
                    stats.put("latest_timestamp", rs.getString("latest"));
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to get history stats", e);
        }
        
        return stats;
    }
    
    /**
     * 重置历史数据库
     */
    public void reset() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS history");
            stmt.execute(CREATE_TABLE_SQL);
            
            log.info("History database reset");
            
        } catch (Exception e) {
            log.error("Failed to reset history database", e);
            throw new RuntimeException("Failed to reset history database", e);
        }
    }
    
    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("History database connection closed");
            }
        } catch (Exception e) {
            log.error("Error closing history database connection", e);
        }
    }
}
