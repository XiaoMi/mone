package run.mone.m78.service.bo.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-04 19:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotPublishRecordBo implements Serializable {
    
    private Long id;

    private Long botId;

    /**
     * 版本记录
     */
    private String versionRecord;

    /**
     * 发布渠道，ex:[1,2,3], id参考m78_im_type
     */
    private String publishImChannel;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

}
