package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.Sla;
import run.mone.mimeter.dashboard.pojo.SlaExample;

public interface SlaMapper {
    long countByExample(SlaExample example);

    int deleteByExample(SlaExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sla record);

    int insertSelective(Sla record);

    List<Sla> selectByExample(SlaExample example);

    Sla selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sla record, @Param("example") SlaExample example);

    int updateByExample(@Param("record") Sla record, @Param("example") SlaExample example);

    int updateByPrimaryKeySelective(Sla record);

    int updateByPrimaryKey(Sla record);

    int batchInsert(@Param("list") List<Sla> list);

    int batchInsertSelective(@Param("list") List<Sla> list, @Param("selective") Sla.Column ... selective);
}