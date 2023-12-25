package run.mone.mimeter.dashboard.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.DomainApplyInfo;
import run.mone.mimeter.dashboard.pojo.DomainApplyInfoExample;

public interface DomainApplyInfoMapper {
    long countByExample(DomainApplyInfoExample example);

    int deleteByExample(DomainApplyInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DomainApplyInfo record);

    int insertSelective(DomainApplyInfo record);

    List<DomainApplyInfo> selectByExampleWithBLOBs(DomainApplyInfoExample example);

    List<DomainApplyInfo> selectByExample(DomainApplyInfoExample example);

    DomainApplyInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DomainApplyInfo record, @Param("example") DomainApplyInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") DomainApplyInfo record, @Param("example") DomainApplyInfoExample example);

    int updateByExample(@Param("record") DomainApplyInfo record, @Param("example") DomainApplyInfoExample example);

    int updateByPrimaryKeySelective(DomainApplyInfo record);

    int updateByPrimaryKeyWithBLOBs(DomainApplyInfo record);

    int updateByPrimaryKey(DomainApplyInfo record);

    int batchInsert(@Param("list") List<DomainApplyInfo> list);

    int batchInsertSelective(@Param("list") List<DomainApplyInfo> list, @Param("selective") DomainApplyInfo.Column ... selective);
}