package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SceneApiInfo;
import run.mone.mimeter.dashboard.pojo.SceneApiInfoExample;

public interface SceneApiInfoMapper {
    long countByExample(SceneApiInfoExample example);

    int deleteByExample(SceneApiInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SceneApiInfo record);

    int insertSelective(SceneApiInfo record);

    List<SceneApiInfo> selectByExampleWithBLOBs(SceneApiInfoExample example);

    List<SceneApiInfo> selectByExample(SceneApiInfoExample example);

    SceneApiInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SceneApiInfo record, @Param("example") SceneApiInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") SceneApiInfo record, @Param("example") SceneApiInfoExample example);

    int updateByExample(@Param("record") SceneApiInfo record, @Param("example") SceneApiInfoExample example);

    int updateByPrimaryKeySelective(SceneApiInfo record);

    int updateByPrimaryKeyWithBLOBs(SceneApiInfo record);

    int updateByPrimaryKey(SceneApiInfo record);

    int batchInsert(@Param("list") List<SceneApiInfo> list);

    int batchInsertSelective(@Param("list") List<SceneApiInfo> list, @Param("selective") SceneApiInfo.Column ... selective);
}