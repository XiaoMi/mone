package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import run.mone.m78.service.dao.entity.M78AsrCostPo;

public interface M78AsrCostMapper extends BaseMapper<M78AsrCostPo> {

    @Update("update m78_asr_cost set used_time = used_time + #{increment},utime = #{utime} where asr_platform = #{asrPlatform} and product_line = #{productLine}")
    int updateUsedTime(@Param("asrPlatform") String asrPlatform, @Param("productLine") String productLine, @Param("increment") Long increment, @Param("utime") Long utime);

    @Update("update m78_asr_cost set used_count = used_count + 1,utime = #{utime} where asr_platform = #{asrPlatform} and product_line = #{productLine}")
    int updateUsedCount(@Param("asrPlatform") String asrPlatform, @Param("productLine") String productLine, @Param("utime") Long utime);
}
