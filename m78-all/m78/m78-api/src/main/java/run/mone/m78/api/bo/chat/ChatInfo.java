package run.mone.m78.api.bo.chat;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/1/15 11:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatInfo implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = true, description = "chatInfoId", defaultValue = "")
    private Long id;

    @HttpApiDocClassDefine(value = "content", required = true, description = "chat内容", defaultValue = "")
    private String content;

    @HttpApiDocClassDefine(value = "mappingContent", required = true, description = "chat映射内容，如sql", defaultValue = "")
    private String mappingContent;

    @HttpApiDocClassDefine(value = "conditions", required = true, description = "解析后的查询条件", defaultValue = "")
    public List<Map<String, Object>> conditions; // TODO: 目前值考虑where中等值条件的修改，后续可能需要修改这里的结构
}
