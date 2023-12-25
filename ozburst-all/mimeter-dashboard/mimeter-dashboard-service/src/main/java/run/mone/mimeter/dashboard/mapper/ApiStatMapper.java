package run.mone.mimeter.dashboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.ApiStat;
import run.mone.mimeter.dashboard.pojo.ApiStatExample;

@Mapper
public interface ApiStatMapper {
    long countByExample(ApiStatExample example);

    int deleteByExample(ApiStatExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiStat record);

    int insertSelective(ApiStat record);

    List<ApiStat> selectByExample(ApiStatExample example);

    ApiStat selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiStat record, @Param("example") ApiStatExample example);

    int updateByExample(@Param("record") ApiStat record, @Param("example") ApiStatExample example);

    int updateByPrimaryKeySelective(ApiStat record);

    int updateByPrimaryKey(ApiStat record);

    int batchInsert(@Param("list") List<ApiStat> list);

    int batchInsertSelective(@Param("list") List<ApiStat> list, @Param("selective") ApiStat.Column ... selective);

    int saveAccumulative(ApiStat record);
}