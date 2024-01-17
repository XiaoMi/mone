package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.MibenchTask;
import run.mone.mimeter.dashboard.pojo.MibenchTaskExample;

public interface MibenchTaskMapper {
    long countByExample(MibenchTaskExample example);

    int deleteByExample(MibenchTaskExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MibenchTask record);

    int insertSelective(MibenchTask record);

    List<MibenchTask> selectByExampleWithBLOBs(MibenchTaskExample example);

    List<MibenchTask> selectByExample(MibenchTaskExample example);

    MibenchTask selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MibenchTask record, @Param("example") MibenchTaskExample example);

    int updateByExampleWithBLOBs(@Param("record") MibenchTask record, @Param("example") MibenchTaskExample example);

    int updateByExample(@Param("record") MibenchTask record, @Param("example") MibenchTaskExample example);

    int updateByPrimaryKeySelective(MibenchTask record);

    int updateByPrimaryKeyWithBLOBs(MibenchTask record);

    int updateByPrimaryKey(MibenchTask record);

    int batchInsert(@Param("list") List<MibenchTask> list);

    int batchInsertSelective(@Param("list") List<MibenchTask> list, @Param("selective") MibenchTask.Column ... selective);
}