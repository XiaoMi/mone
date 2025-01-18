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
import static run.mone.m78.api.constant.TableConstant.INVOKE_HISTORY_DETAIL_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(INVOKE_HISTORY_DETAIL_TABLE)
public class M78InvokeHistoryDetailPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column
    private Integer type; // 类型, 1bot, 2flow, 3plugin

    @Column("relate_id")
    private Long relateId;

    private String inputs;

    private String outputs;

    @Column("invoke_time")
    private Long invokeTime;

    @Column("invoke_way")
    private Integer invokeWay; // 调用方式, 1页面, 2接口, 3系统内部, 4调试等等

    @Column("invoke_user_name")
    private String invokeUserName;
}
