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

import static run.mone.m78.api.constant.TableConstant.CHAT_INFO_TABLE;


/**
 * @author HawickMason@xiaomi.com
 * @date 1/15/24 10:20 AM
 * <p>
 * 文档提问的时候,这里存储那些问题(以后会更泛化)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CHAT_INFO_TABLE)
public class ChatInfoPo {


    @Id(keyType = KeyType.Auto)
    private Long id;

    //外键
    @Column("session_id")
    private String sessionId;

    /**
     * 单条聊天记录
     */
    @Column("content")
    private String content;

    /**
     * 聊天记录对应映射内容，eg: sql
     */
    @Column("mapping_content")
    private String mappingContent;

    /**
     * 解析后的查询条件
     */
    @Column(value = "conditions")
    private String conditions;

    /**
     * 用户名
     */
    @Column("user_name")
    private String user;

    /**
     * TODO: 热度， 排序预留(也可考虑不持久化...)
     */
    @Column("heat")
    private Long heat;

    // TODO: 预留, 软删除标记
    @Column("status")
    private Integer status;

    // store only
    @Column("create_time")
    private String createTime;

    // store only
    @Column("modify_time")
    private String updateTime;

    @Builder.Default
    @Column("type")
    private Integer type = 0;

    //存储一些元数据
    @Column(value = "chat_info_meta", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, String> chatInfoMeta;
}
