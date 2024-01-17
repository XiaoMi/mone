package run.mone.mimeter.dashboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.ReportStat;
import run.mone.mimeter.dashboard.pojo.ReportStatExample;

@Mapper
public interface ReportStatMapper {
    long countByExample(ReportStatExample example);

    int deleteByExample(ReportStatExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ReportStat record);

    int insertSelective(ReportStat record);

    List<ReportStat> selectByExample(ReportStatExample example);

    ReportStat selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ReportStat record, @Param("example") ReportStatExample example);

    int updateByExample(@Param("record") ReportStat record, @Param("example") ReportStatExample example);

    int updateByPrimaryKeySelective(ReportStat record);

    int updateByPrimaryKey(ReportStat record);

    int batchInsert(@Param("list") List<ReportStat> list);

    int batchInsertSelective(@Param("list") List<ReportStat> list, @Param("selective") ReportStat.Column ... selective);

    int saveAccumulative(ReportStat record);
}