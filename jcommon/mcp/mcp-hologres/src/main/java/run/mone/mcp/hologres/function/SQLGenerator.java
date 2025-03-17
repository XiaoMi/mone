package run.mone.mcp.hologres.function;

import com.blinkfox.zealot.bean.SqlInfo;
import com.blinkfox.zealot.core.ZealotKhala;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Slf4j
public class SQLGenerator {

    /**
     * 根据表名和时间生成 SQL 查询语句
     * https://github.com/blinkfox/zealot
     * @param tableName 表名
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime   结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 生成的 SQL 查询语句
     */
    public static SqlInfo generateSQLQuery(String tableName, String startTime, String endTime, Integer count) {
        // 验证时间格式
        // 验证时间格式
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String startDate = LocalDate.parse(startTime).format(formatter);
//        String endDate = LocalDate.parse(endTime).format(formatter);
        int num = count == null ? 5 : count;
        return ZealotKhala.start()
                .select("*")
                .from(tableName)
                .where("1=1")
                .andBetween("create_date", startTime, endTime)
                .limit(String.valueOf(num))
                .end();
    }

    public static void main(String[] args) {
        generateSQLQuery("dig_org", "2019-01-01", "2019-01-01", null);
    }

}