package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import run.mone.m78.service.dao.entity.M78CategoryPluginRel;
import run.mone.m78.service.dao.entity.M78CategoryPluginRelName;

import java.util.List;

/**
 *  映射层。
 *
 * @author hoho
 * @since 2024-03-01
 */
public interface M78CategoryPluginRelMapper extends BaseMapper<M78CategoryPluginRel> {

    @Select("select a.plugin_id pluginId, b.name name from m78_category_plugin_rel a inner join m78_category b on a.cat_id = b.id where a.plugin_id in ${pluginIds} and a.deleted = 0;")
    List<M78CategoryPluginRelName> getCategoryByPlugins(@Param("pluginIds") String pluginIds);
}
