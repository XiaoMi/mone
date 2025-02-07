package run.mone.m78.service.dto;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-05 20:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotBaseInfoDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", description = "机器人id")
    private Long id;

    @HttpApiDocClassDefine(value = "name", description = "机器人名称")
    private String name;

    @HttpApiDocClassDefine(value = "avatarUrl", description = "机器人头像")
    private String avatarUrl;

    @HttpApiDocClassDefine(value = "creator", description = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "remark", description = "机器人描述")
    private String remark;

    @HttpApiDocClassDefine(value = "categoryList", description = "所属分类")
    private List<CategoryDto> categoryList;

    @HttpApiDocClassDefine(value = "useTimes", description = "使用次数")
    private Long useTimes;

}
