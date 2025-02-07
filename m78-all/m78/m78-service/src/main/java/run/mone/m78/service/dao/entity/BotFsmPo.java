package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.BOT_FSM_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(BOT_FSM_TABLE)
public class BotFsmPo implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("app_id")
    private Integer appId;

    @Column("bot_id")
    private Long botId;

    @Column("username")
    private String username;

    @Column
    private Integer state;

    /**
     * 是否删除 0-否 1-是
     */
    private int deleted;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column(value = "meta", typeHandler = GsonTypeHandler.class)
    private Map<String, String> meta;

}
