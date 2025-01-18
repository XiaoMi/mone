package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import run.mone.m78.service.dao.entity.M78BotPluginOrg;

/**
 *  映射层。
 *
 * @author mason
 * @since 2024-03-02
 */
public interface M78BotPluginOrgMapper extends BaseMapper<M78BotPluginOrg> {

    @Update("update m78_bot_plugin_org set plugin_avg_star = (select ROUND(AVG(score), 1) from m78_bot_comment where item_id = #{orgId} and type = 1) where id = #{orgId}")
    int updateAvgScore(@Param("orgId") Long orgId);
}
