package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.ReportInfo;
import run.mone.mimeter.dashboard.pojo.ReportInfoExample;

public interface ReportInfoMapper {
    long countByExample(ReportInfoExample example);

    int deleteByExample(ReportInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ReportInfo record);

    int insertSelective(ReportInfo record);

    List<ReportInfo> selectByExampleWithBLOBs(ReportInfoExample example);

    List<ReportInfo> selectByExample(ReportInfoExample example);

    ReportInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ReportInfo record, @Param("example") ReportInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") ReportInfo record, @Param("example") ReportInfoExample example);

    int updateByExample(@Param("record") ReportInfo record, @Param("example") ReportInfoExample example);

    int updateByPrimaryKeySelective(ReportInfo record);

    int updateByPrimaryKeyWithBLOBs(ReportInfo record);

    int updateByPrimaryKey(ReportInfo record);

    int batchInsert(@Param("list") List<ReportInfo> list);

    int batchInsertSelective(@Param("list") List<ReportInfo> list, @Param("selective") ReportInfo.Column ... selective);
}