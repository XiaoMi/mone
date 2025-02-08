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
import run.mone.m78.api.enums.MultiModalCmdTypeEnum;

import java.util.List;

import static run.mone.m78.api.constant.TableConstant.BOT_MULTI_MODAL_HISTORY;

/**
 * @author wmin
 * @date 2024/7/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(BOT_MULTI_MODAL_HISTORY)
public class M78MultiModalHistoryPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column
    private Long workSpaceId;

    @Column
    private String taskId;

    /**
     * @see MultiModalCmdTypeEnum
     */
    @Column
    private Integer type;

    @Column
    private String aiModel;

    @Column
    private Integer deleted;

    /**
     * 0 生成中
     * 1 成功
     * 2 失败
     */
    @Column
    private Integer runStatus;

    @Column
    private String userName;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column(value = "multi_modal_resource_output", typeHandler = Fastjson2TypeHandler.class)
    private List<String> multiModalResourceOutput;

    @Column(value = "setting", typeHandler = Fastjson2TypeHandler.class)
    private Object setting;

    @Column("rst_message")
    private String rstMessage;
}
