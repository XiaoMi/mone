package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.CheckPointInfo;
import run.mone.mimeter.dashboard.pojo.CheckPointInfoExample;

public interface CheckPointInfoMapper {
    long countByExample(CheckPointInfoExample example);

    int deleteByExample(CheckPointInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CheckPointInfo record);

    int insertSelective(CheckPointInfo record);

    List<CheckPointInfo> selectByExampleWithBLOBs(CheckPointInfoExample example);

    List<CheckPointInfo> selectByExample(CheckPointInfoExample example);

    CheckPointInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CheckPointInfo record, @Param("example") CheckPointInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") CheckPointInfo record, @Param("example") CheckPointInfoExample example);

    int updateByExample(@Param("record") CheckPointInfo record, @Param("example") CheckPointInfoExample example);

    int updateByPrimaryKeySelective(CheckPointInfo record);

    int updateByPrimaryKeyWithBLOBs(CheckPointInfo record);

    int updateByPrimaryKey(CheckPointInfo record);

    int batchInsert(@Param("list") List<CheckPointInfo> list);

    int batchInsertSelective(@Param("list") List<CheckPointInfo> list, @Param("selective") CheckPointInfo.Column ... selective);
}