package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.INVOKE_SUMMARY_PERDAY_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(INVOKE_SUMMARY_PERDAY_TABLE)
public class M78InvokeSummaryPerdayPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column
    private Integer type; // 类型, 1bot, 2flow, 3plugin

    @Column("relate_id")
    private Long relateId;

    @Column("invoke_counts")
    private Long invokeCounts;

    @Column("invoke_users")
    private Long invokeUsers;

    @Column("invoke_day")
    private Long invokeDay;
}
