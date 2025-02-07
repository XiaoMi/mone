package run.mone.m78.api.bo.table;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/15/24 16:11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTableReq implements Serializable {

    private static final long serialVersionUID = -420519440091860590L;

    @HttpApiDocClassDefine(value = "tableDesc", description = "表注释")
    private String tableDesc;

    @HttpApiDocClassDefine(value = "model", description = "模型")
    private String model;

    @HttpApiDocClassDefine(value = "botId", description = "机器人id")
    private Long botId;

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "columnInfoList", description = "列信息")
    private List<M78ColumnInfo> columnInfoList;

    @HttpApiDocClassDefine(value = "ddl", description = "如果是ai生成的ddl，则将生成的ddl传递过来, 否则不用传递")
    private String ddl;

    @HttpApiDocClassDefine(value = "tableName", description = "表名")
    private String tableName;

    @HttpApiDocClassDefine(value = "id", description = "表id")
    private Long id;
}
