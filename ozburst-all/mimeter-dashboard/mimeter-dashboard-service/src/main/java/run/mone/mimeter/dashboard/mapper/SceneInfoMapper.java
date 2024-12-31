package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SceneInfo;
import run.mone.mimeter.dashboard.pojo.SceneInfoExample;

public interface SceneInfoMapper {
    long countByExample(SceneInfoExample example);

    int deleteByExample(SceneInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SceneInfo record);

    int insertSelective(SceneInfo record);

    List<SceneInfo> selectByExampleWithBLOBs(SceneInfoExample example);

    List<SceneInfo> selectByExample(SceneInfoExample example);

    SceneInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SceneInfo record, @Param("example") SceneInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") SceneInfo record, @Param("example") SceneInfoExample example);

    int updateByExample(@Param("record") SceneInfo record, @Param("example") SceneInfoExample example);

    int updateByPrimaryKeySelective(SceneInfo record);

    int updateByPrimaryKeyWithBLOBs(SceneInfo record);

    int updateByPrimaryKey(SceneInfo record);

    int batchInsert(@Param("list") List<SceneInfo> list);

    int batchInsertSelective(@Param("list") List<SceneInfo> list, @Param("selective") SceneInfo.Column ... selective);
}