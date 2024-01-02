package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.Dataset;
import run.mone.mimeter.dashboard.pojo.DatasetExample;

public interface DatasetMapper {
    long countByExample(DatasetExample example);

    int deleteByExample(DatasetExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Dataset record);

    int insertSelective(Dataset record);

    List<Dataset> selectByExampleWithBLOBs(DatasetExample example);

    List<Dataset> selectByExample(DatasetExample example);

    Dataset selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByExampleWithBLOBs(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByExample(@Param("record") Dataset record, @Param("example") DatasetExample example);

    int updateByPrimaryKeySelective(Dataset record);

    int updateByPrimaryKeyWithBLOBs(Dataset record);

    int updateByPrimaryKey(Dataset record);

    int batchInsert(@Param("list") List<Dataset> list);

    int batchInsertSelective(@Param("list") List<Dataset> list, @Param("selective") Dataset.Column ... selective);
}