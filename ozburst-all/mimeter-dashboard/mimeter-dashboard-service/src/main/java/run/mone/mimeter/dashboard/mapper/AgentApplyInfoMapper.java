package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.AgentApplyInfo;
import run.mone.mimeter.dashboard.pojo.AgentApplyInfoExample;

public interface AgentApplyInfoMapper {
    long countByExample(AgentApplyInfoExample example);

    int deleteByExample(AgentApplyInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AgentApplyInfo record);

    int insertSelective(AgentApplyInfo record);

    List<AgentApplyInfo> selectByExample(AgentApplyInfoExample example);

    AgentApplyInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AgentApplyInfo record, @Param("example") AgentApplyInfoExample example);

    int updateByExample(@Param("record") AgentApplyInfo record, @Param("example") AgentApplyInfoExample example);

    int updateByPrimaryKeySelective(AgentApplyInfo record);

    int updateByPrimaryKey(AgentApplyInfo record);

    int batchInsert(@Param("list") List<AgentApplyInfo> list);

    int batchInsertSelective(@Param("list") List<AgentApplyInfo> list, @Param("selective") AgentApplyInfo.Column ... selective);
}