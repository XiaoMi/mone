package run.mone.m78.service.bo.bot;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-07 14:07
 */
@Data
public class BotExtensionBo implements Serializable {

    @HttpApiDocClassDefine(value = "pluginId", description = "插件id")
    private List<Long> pluginId;

    @HttpApiDocClassDefine(value = "flowBaseId", description = "工作流id")
    private List<Long> flowBaseId;

    @HttpApiDocClassDefine(value = "knowledgeBaseId", description = "知识库id")
    private List<Long> knowledgeBaseId;

    @HttpApiDocClassDefine(value = "dbTableId", description = "数据库表")
    private List<Long> dbTableId;

}
