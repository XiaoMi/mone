package run.mone.m78.api.bo.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dp
 * @since 2024-03-13
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PublishRecordDTO implements Serializable {

    private Long id;

    private Long botId;

    /**
     * 版本记录
     */
    private String versionRecord;

    private String botSnapshot;

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
