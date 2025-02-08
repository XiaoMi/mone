package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import run.mone.m78.service.dto.BotPublishDto;

/**
 * 实体类。
 *
 * @author hoho
 * @since 2024-03-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_bot_publish_record")
public class M78BotPublishRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long botId;

    /**
     * 版本记录
     */
    private String versionRecord;

    /**
     * bot快照
     */
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

    public M78BotPublishRecord(BotPublishDto publishDto) {
        BeanUtils.copyProperties(publishDto, this);
    }
}
