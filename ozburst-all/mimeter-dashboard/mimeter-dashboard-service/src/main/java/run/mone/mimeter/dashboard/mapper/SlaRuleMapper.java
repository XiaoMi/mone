package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SlaRule;
import run.mone.mimeter.dashboard.pojo.SlaRuleExample;

public interface SlaRuleMapper {
    long countByExample(SlaRuleExample example);

    int deleteByExample(SlaRuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SlaRule record);

    int insertSelective(SlaRule record);

    List<SlaRule> selectByExample(SlaRuleExample example);

    SlaRule selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SlaRule record, @Param("example") SlaRuleExample example);

    int updateByExample(@Param("record") SlaRule record, @Param("example") SlaRuleExample example);

    int updateByPrimaryKeySelective(SlaRule record);

    int updateByPrimaryKey(SlaRule record);

    int batchInsert(@Param("list") List<SlaRule> list);

    int batchInsertSelective(@Param("list") List<SlaRule> list, @Param("selective") SlaRule.Column ... selective);
}