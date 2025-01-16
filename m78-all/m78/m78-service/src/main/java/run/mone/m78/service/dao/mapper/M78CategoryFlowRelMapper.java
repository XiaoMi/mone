package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;
import run.mone.m78.service.dao.entity.M78Category;
import run.mone.m78.service.dao.entity.M78CategoryFlowRel;
import run.mone.m78.service.dao.entity.M78CategoryPluginRel;

import java.util.List;

/**
 * @author liuchuankang
 * @Type CategoryFlowRelMapper.java
 * @Desc
 * @date 2024/8/21 10:53
 */
public interface M78CategoryFlowRelMapper extends BaseMapper<M78CategoryFlowRel> {
	@Select("select rel.cat_id catId,rel.flow_id flowId from m78_category_flow_rel  rel where rel.cat_id = #{categoryId} and rel.deleted = 0")
	List<M78CategoryFlowRel> getByCatId(Long categoryId);
	@Select("select  cat.name from m78_category as cat left join m78_category_flow_rel flowrel on cat.id=flowrel.cat_id where flowrel.flow_id = #{flowId} and flowrel.deleted = 0")
	List<String> getCatNameByFlowId(Integer flowId);



}
