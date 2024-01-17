package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.AgentInfo;
import run.mone.mimeter.dashboard.pojo.AgentInfoExample;

public interface AgentInfoMapper {
    long countByExample(AgentInfoExample example);

    int deleteByExample(AgentInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AgentInfo record);

    int insertSelective(AgentInfo record);

    List<AgentInfo> selectByExampleWithBLOBs(AgentInfoExample example);

    List<AgentInfo> selectByExample(AgentInfoExample example);

    AgentInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AgentInfo record, @Param("example") AgentInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") AgentInfo record, @Param("example") AgentInfoExample example);

    int updateByExample(@Param("record") AgentInfo record, @Param("example") AgentInfoExample example);

    int updateByPrimaryKeySelective(AgentInfo record);

    int updateByPrimaryKeyWithBLOBs(AgentInfo record);

    int updateByPrimaryKey(AgentInfo record);

    int batchInsert(@Param("list") List<AgentInfo> list);

    int batchInsertSelective(@Param("list") List<AgentInfo> list, @Param("selective") AgentInfo.Column ... selective);
}