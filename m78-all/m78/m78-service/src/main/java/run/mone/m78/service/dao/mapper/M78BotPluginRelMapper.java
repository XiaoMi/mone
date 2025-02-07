package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import run.mone.m78.service.dao.entity.M78BotPluginRel;
import run.mone.m78.service.dao.entity.M78PluginRelCount;

import java.util.List;

/**
 *  映射层。
 *
 * @author hoho
 * @since 2024-03-07
 */
public interface M78BotPluginRelMapper extends BaseMapper<M78BotPluginRel> {

    @Select("select a.org_id org_id, sum(b.count) count\n" +
            "from m78_bot_plugin a \n" +
            "inner join \n" +
            "(select plugin_id, count(1) count \n" +
            "from m78_bot_plugin_rel where plugin_id in (select distinct id from m78_bot_plugin where org_id in ${orgIds}) and deleted=0 group by plugin_id) b on a.id=b.plugin_id group by a.org_id;")
    List<M78PluginRelCount> getOrgRelCount(@Param("orgIds") String orgIds);
}
