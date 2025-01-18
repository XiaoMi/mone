package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.INVOKE_HISTORY_DETAIL_TABLE;
import static run.mone.m78.api.constant.TableConstant.OPENAPI_ASYNC_TASK_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(OPENAPI_ASYNC_TASK_TABLE)
public class M78OpenapiAsyncTaskPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    // 类型, 1bot-exec, 2flow-exec
    @Column
    private Integer type;

    @Column("relate_id")
    private Long relateId;

    private String inputs;

    private String outputs;

    @Column("task_id")
    private String taskId;

    @Column("task_status")
    private Integer taskStatus;

    @Column("callback_url")
    private String callbackUrl;

    @Column("callback_status")
    private Integer callbackStatus;

    @Column("invoke_start_time")
    private Long invokeStartTime;

    @Column("invoke_end_time")
    private Long invokeEndTime;

    @Column("invoke_user_name")
    private String invokeUserName;

}
