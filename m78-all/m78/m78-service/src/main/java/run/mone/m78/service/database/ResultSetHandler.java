package run.mone.m78.service.database;

import com.mybatisflex.core.row.Row;

import java.sql.SQLException;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/5/24 15:15
 */
@FunctionalInterface
public interface ResultSetHandler<T> {
    T handle(List<Row> var1) throws SQLException;
}
