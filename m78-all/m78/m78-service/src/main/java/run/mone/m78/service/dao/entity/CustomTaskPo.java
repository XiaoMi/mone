package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.CUSTOM_TASK_TABLE;

/**
 * @author zhangping17
 * @date 2/29/24 15:39
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CUSTOM_TASK_TABLE)
public class CustomTaskPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("task_name")
    private String taskName;

    /**
     * 任务类型, 1: 单词执行, 2: 周期执行
     */
    @Column("task_type")
    private Integer taskType;

    @Column(value = "task_detail", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> taskDetail;

    @Column(value = "scheduled_time")
    private String scheduledTime;

    private Integer status;

    @Column("user_name")
    private String userName;

    @Column("input")
    private String input;

    private Long ctime;

    private Long utime;

    @Column("bot_id")
    private Long botId;

    @Column("moon_id")
    private Long moonId;

    @Column("core_type")
    private String coreType;
}
