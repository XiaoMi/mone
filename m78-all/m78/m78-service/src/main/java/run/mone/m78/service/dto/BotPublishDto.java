package run.mone.m78.service.dto;

import com.xiaomi.mone.docs.annotations.http.HttpApiDocClassDefine;
import jnr.ffi.annotations.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-04 19:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotPublishDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", description = "id")
    private Long id;

    @HttpApiDocClassDefine(value = "botId", description = "机器人id")
    private Long botId;

    /**
     * 版本记录
     */
    @HttpApiDocClassDefine(value = "versionRecord", description = "版本记录")
    private String versionRecord;

    /**
     * 发布渠道，ex:[1,2,3], id参考m78_im_type
     */
    @HttpApiDocClassDefine(value = "publishImChannel", description = "发布渠道")
    private List<Integer> publishImChannel;

    /**
     * 发布时间
     */
    @HttpApiDocClassDefine(value = "publishTime", description = "发布时间")
    private LocalDateTime publishTime;

    @HttpApiDocClassDefine(value = "publisher", description = "发布人")
    private String publisher;

    /**
     * 权限，是否公开
     */
    @HttpApiDocClassDefine(value = "permissions", description = "权限")
    private Integer permissions;

    private List<Long> categoryIds;

    /**
     * 发布渠道的openId
     */
    @HttpApiDocClassDefine(value = "openId", description = "openId")
    private String openId;

}
