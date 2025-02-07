package run.mone.m78.api.bo.feature.router;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 15:03
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FeatureRouterReq implements Serializable {

    private static final long serialVersionUID = 5034370673249708885L;
    /**
     * FeatureRouter的Id
     */
    @HttpApiDocClassDefine(value = "id", required = false, description = "FeatureRouter的Id", defaultValue = "")
    private Long id;

    /**
     * 关联的chatInfo的id
     */
    @HttpApiDocClassDefine(value = "labelId", required = true, description = "关联的chatInfo的id", defaultValue = "")
    private Long labelId;

    /**
     * 使用数据源管理时传递数据源id; 文档上传场景中置空
     */
    @HttpApiDocClassDefine(value = "datasourceId", required = true, description = "使用数据源管理时传递数据源id; 文档上传场景中置空", defaultValue = "")
    private Long datasourceId;

    /**
     * featureRouter的name
     */
    @HttpApiDocClassDefine(value = "name", required = true, description = "featureRouter的name", defaultValue = "")
    private String name;

    @HttpApiDocClassDefine(value = "userName", required = false, description = "userName", defaultValue = "")
    private String userName;

    //导出http调用时，请求的参数
    @HttpApiDocClassDefine(value = "reqData", required = false, description = "导出http调用时，请求的参数", defaultValue = "")
    private Map<String, Object> reqData;

    @HttpApiDocClassDefine(value = "type", required = false, description = "类型, 0: sql, 1: script", defaultValue = "0")
    private Integer type;

    @HttpApiDocClassDefine(value = "routerType", required = false, description = "router类型, 0: ChatBased, 1: Probot", defaultValue = "0")
    private Integer routerType;

    @HttpApiDocClassDefine(value = "page", required = false, description = "pageNum, 和pageSize需同时传递，否则查询全量", defaultValue = "")
    private Integer page;

    @HttpApiDocClassDefine(value = "pageSize", required = false, description = "pageSize, 和pageSize需同时传递，否则查询全量", defaultValue = "")
    private Integer pageSize;

    @HttpApiDocClassDefine(value = "content", required = false, description = "更新关联的mappingContent, 注意为null时不更新", defaultValue = "")
    private String content;
}
