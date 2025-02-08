package run.mone.m78.api.bo.table;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/18/24 20:01
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTableUpdateReq {

    @HttpApiDocClassDefine(value = "columnInfoList", description = "列信息")
    private List<M78ColumnInfo> columnInfoList;

    @HttpApiDocClassDefine(value = "tableName", description = "表名")
    private String tableName;

    @HttpApiDocClassDefine(value = "botId", description = "机器人id")
    private Long botId;

    @HttpApiDocClassDefine(value = "workspaceId", description = "workspaceId")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "demo", description = "示例内容")
    private String demo;

    @HttpApiDocClassDefine(value = "comment", description = "表注释")
    private String tableDesc;
}
