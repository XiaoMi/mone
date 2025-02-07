package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static run.mone.m78.api.constant.TableConstant.MULTIMODAL_LIMIT_TABLE;

/**
 * @author zhangxiaowei6
 * @Date 2024/12/13 16:10
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(MULTIMODAL_LIMIT_TABLE)
public class MultimodalLimitPo {
    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column("user")
    private String user;

    @Column("type")
    private int type;

    @Column("count")
    private int count;

    @Column("limit_day")
    private String limitDay;

}
