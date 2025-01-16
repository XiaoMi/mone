package run.mone.m78.service.bo.database;

import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.database.ResultSetHandler;
import run.mone.m78.service.exceptions.QueryException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/5/24 14:43
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QueryRunner {

    private String dataSourceKey;

    private DataSource dataSource;

    public <T> T query(String sql, ResultSetHandler<T> resultSetHandler, Object... args) {
        try {
            DataSourceKey.use(this.dataSourceKey);
            return resultSetHandler.handle(Db.selectListBySql(sql, args));
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            DataSourceKey.clear();
        }
    }

    public int update(String sql, Object... args) {
        try {
            DataSourceKey.use(this.dataSourceKey);
            return Db.updateBySql(sql, args);
        } finally {
            DataSourceKey.clear();
        }
    }
}
