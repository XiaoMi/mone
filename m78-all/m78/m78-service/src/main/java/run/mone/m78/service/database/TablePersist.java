package run.mone.m78.service.database;

import run.mone.m78.service.bo.database.FieldCmtBO;
import run.mone.m78.service.bo.database.MetaBO;
import run.mone.m78.service.bo.database.TableBO;

import java.util.List;
import java.util.Map;

/**
 * 表格数据持久化接口
 *
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 5:18 PM
 */
public interface TablePersist {

    /**
     * 根据传入的sql创建表, 需要调用侧确保表名符合规则(m78_为前缀)
     *
     * @param createSql 建表sql语句
     * @param force     是否强制创建(当建表语句重复的时候), 待实现
     * @return
     */
    TableBO createTableBySql(String createSql, boolean force);

    /**
     * 根据传入的元信息创建表
     *
     * @param meta 创建表所需的元信息
     * @return
     */
    TableBO createTableByMeta(MetaBO meta, boolean force);

    /**
     * 向指定表中插入数据
     *
     * @param tableName 要插入的表名
     * @param values    插入的数据，k(列名) v(值)
     * @return
     */
    TableBO insert2Table(String tableName, Map<String/*colName*/, Object/*value*/> values);

    TableBO insert2TableBatch(String tableName, List<Map<String/*colName*/, Object/*value*/>> values);

    /**
     * 根据提供的SQL插入语句向数据库表中插入数据，并返回插入操作的结果对象TableBO
     *
     * @param sql 插入的sql
     * @return TableBO
     */
    TableBO insert2TableByInsertSql(String sql);

    /**
     * 执行sql查询
     *
     * @param sql 要执行的sql
     * @return
     */
    TableBO query(String sql);

    /**
     * 获取该表的建表语句
     *
     * @param tableName
     * @return
     */
    String getCreateTableStatement(String tableName);

    /**
     * 获取该表字段与对应注释的映射
     * @param tableName
     * @return
     */
    List<FieldCmtBO> getFieldCommentMapping(String tableName);

    /**
     * 检查给定的表名是否在数据库中存在，存在则返回true，否则返回false。
     */
    boolean checkExistTable(String tableName);

    /**
     * 根据给定的表名返回对应表的表头列表
     */
    List<String> getTableHeaderByTableName(String tableName);

    void updateColumnComment(String tableName, String columnName, String comment);
}
