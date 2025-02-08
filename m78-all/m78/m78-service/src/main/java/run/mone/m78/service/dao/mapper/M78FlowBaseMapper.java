package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import run.mone.m78.service.dao.entity.FlowBasePo;


public interface M78FlowBaseMapper extends BaseMapper<FlowBasePo> {
	@Update("update m78_flow_base set flow_avg_star = (select ROUND(AVG(score), 1) from m78_bot_comment where item_id = #{flowId} and type = 2) where id = #{flowId}")
	int updateAvgScore(@Param("flowId") Long flowId);
}
