package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.DatasetSceneRelation;
import run.mone.mimeter.dashboard.pojo.DatasetSceneRelationExample;

public interface DatasetSceneRelationMapper {
    long countByExample(DatasetSceneRelationExample example);

    int deleteByExample(DatasetSceneRelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DatasetSceneRelation record);

    int insertSelective(DatasetSceneRelation record);

    List<DatasetSceneRelation> selectByExample(DatasetSceneRelationExample example);

    DatasetSceneRelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DatasetSceneRelation record, @Param("example") DatasetSceneRelationExample example);

    int updateByExample(@Param("record") DatasetSceneRelation record, @Param("example") DatasetSceneRelationExample example);

    int updateByPrimaryKeySelective(DatasetSceneRelation record);

    int updateByPrimaryKey(DatasetSceneRelation record);

    int batchInsert(@Param("list") List<DatasetSceneRelation> list);

    int batchInsertSelective(@Param("list") List<DatasetSceneRelation> list, @Param("selective") DatasetSceneRelation.Column ... selective);
}