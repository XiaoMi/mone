package run.mone.m78.api.bo.datasource;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/19 17:05
 */
@Data
public class TableAlterationDTO implements Serializable {

    @HttpApiDocClassDefine(value = "datasourceId", required = true, description = "数据源的Id", defaultValue = "")
    private Integer datasourceId;

    @HttpApiDocClassDefine(value = "tableName", required = true, description = "表名", defaultValue = "")
    private String tableName;

    /**
     *   String operationType = columnOperation.get("operationType");
     *                 String columnName = columnOperation.get("columnName");
     *                 String columnType = columnOperation.get("columnType");
     *                 String nullable = columnOperation.get("nullable");
     *                 String defaultValue = columnOperation.get("defaultValue");
     *                 String comment = columnOperation.get("comment"); // 新增comment字段
     */
    @HttpApiDocClassDefine(value = "columnOperations", required = true, description = "列操作，内容如下：[\n" +
            "        {\n" +
            "        \t\"operationType\": \"操作类型，包括ADD/MODIFY/DROP，分别对应新增一列，修改一列，删除一列\"\n" +
            "            \"columnType\": \"bigint(20) unsigned\",\n" +
            "            \"columnComment\": \"修改后的列注释\",\n" +
            "            \"columnName\": \"列名\",\n" +
            "            \"nullable\": \"约束，非必传\",\n" +
            "            \"defaultValue\": \"默认值，非必传\"\n" +
            "        }\n" +
            "    ]", defaultValue = "")
    private List<Map<String, String>> columnOperations;

}
