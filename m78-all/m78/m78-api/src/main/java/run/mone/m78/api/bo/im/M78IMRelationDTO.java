package run.mone.m78.api.bo.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 *  实体类。
 *
 * @author zhangping17
 * @since 2024-03-05
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78IMRelationDTO implements Serializable {

    private BigInteger id;

    private BigInteger botId;

    private String botName;

    private Integer imTypeId;

    private String relationFlag;

    private String creator;

    private Integer deleted;

    private LocalDateTime createTime;
}
