package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.FEATURE_ROUTER_TABLE;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 14:53
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(FEATURE_ROUTER_TABLE)
public class FeatureRouter {

    @Id(keyType = KeyType.Auto)
    private long id;

    @Column
    private String name;

    @Column("user_name")
    private String userName;

    //底层存储是chatInfoPo
    @Column("label_id")
    private Long labelId;

    @Column("status")
    private Integer status;

    @Column("create_time")
    private String createTime;

    @Column("modify_time")
    private String updateTime;

    @Builder.Default
    @Column("type")
    private Integer type = 0; //0: 基于聊天导出的featureRouter类型, 1: Probot导入的featureRouter类型

    @Column(value = "router_meta", typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> routerMeta;

    @RelationOneToOne(selfField = "label_id", targetField = "id")
    private ChatInfoPo chatInfoPo;

}
