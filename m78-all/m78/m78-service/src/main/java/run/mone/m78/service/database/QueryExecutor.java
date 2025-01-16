package run.mone.m78.service.database;

import run.mone.m78.service.dao.entity.QueryContext;
import run.mone.m78.service.dao.entity.TableInfo;
import run.mone.m78.service.exceptions.QueryException;

import java.util.List;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:15 PM
 */
public interface QueryExecutor {

    /**
     * 返回SQL结果集行数
     *
     * @param sql Dialect处理后的SQL
     * @param context
     * @return
     * @throws QueryException
     */
    Integer getRowCount(String sql, QueryContext context) throws QueryException;


    /**
     * 返回查询结果集
     *
     * @param context
     * @return
     * @throws QueryException
     */
    List<Map<String, Object>> exec(String sql, QueryContext context) throws QueryException;

    /**
     * 根据数据源获得数据源下的表信息
     *
     * @param datasourceId
     * @return
     */
    List<TableInfo> getTableInfos(String datasourceId);

    /**
     * 根据数据源和表名获得字段信息
     *
     * @param datasourceId
     * @param tableName
     * @return
     */
    List<String> getFields(String datasourceId, String tableName);
}
