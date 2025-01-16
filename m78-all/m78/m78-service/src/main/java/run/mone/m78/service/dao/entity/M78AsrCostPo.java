package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_asr_cost")
public class M78AsrCostPo {
    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("product_line")
    private String productLine;

    @Column("asr_platform")
    private String asrPlatform;

    @Column("used_time")
    private Long usedTime;

    @Column("used_count")
    private Integer usedCount;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

}
