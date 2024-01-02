package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SceneGroup;
import run.mone.mimeter.dashboard.pojo.SceneGroupExample;

public interface SceneGroupMapper {
    long countByExample(SceneGroupExample example);

    int deleteByExample(SceneGroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SceneGroup record);

    int insertSelective(SceneGroup record);

    List<SceneGroup> selectByExample(SceneGroupExample example);

    SceneGroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SceneGroup record, @Param("example") SceneGroupExample example);

    int updateByExample(@Param("record") SceneGroup record, @Param("example") SceneGroupExample example);

    int updateByPrimaryKeySelective(SceneGroup record);

    int updateByPrimaryKey(SceneGroup record);

    int batchInsert(@Param("list") List<SceneGroup> list);

    int batchInsertSelective(@Param("list") List<SceneGroup> list, @Param("selective") SceneGroup.Column ... selective);
}