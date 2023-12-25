package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SceneSnapshot;
import run.mone.mimeter.dashboard.pojo.SceneSnapshotExample;

public interface SceneSnapshotMapper {
    long countByExample(SceneSnapshotExample example);

    int deleteByExample(SceneSnapshotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SceneSnapshot record);

    int insertSelective(SceneSnapshot record);

    List<SceneSnapshot> selectByExampleWithBLOBs(SceneSnapshotExample example);

    List<SceneSnapshot> selectByExample(SceneSnapshotExample example);

    SceneSnapshot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SceneSnapshot record, @Param("example") SceneSnapshotExample example);

    int updateByExampleWithBLOBs(@Param("record") SceneSnapshot record, @Param("example") SceneSnapshotExample example);

    int updateByExample(@Param("record") SceneSnapshot record, @Param("example") SceneSnapshotExample example);

    int updateByPrimaryKeySelective(SceneSnapshot record);

    int updateByPrimaryKeyWithBLOBs(SceneSnapshot record);

    int updateByPrimaryKey(SceneSnapshot record);

    int batchInsert(@Param("list") List<SceneSnapshot> list);

    int batchInsertSelective(@Param("list") List<SceneSnapshot> list, @Param("selective") SceneSnapshot.Column ... selective);
}