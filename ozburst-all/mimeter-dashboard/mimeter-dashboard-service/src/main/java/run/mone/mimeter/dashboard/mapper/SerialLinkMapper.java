package run.mone.mimeter.dashboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import run.mone.mimeter.dashboard.pojo.SerialLink;
import run.mone.mimeter.dashboard.pojo.SerialLinkExample;

@Mapper
public interface SerialLinkMapper {
    long countByExample(SerialLinkExample example);

    int deleteByExample(SerialLinkExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SerialLink record);

    int insertSelective(SerialLink record);

    List<SerialLink> selectByExample(SerialLinkExample example);

    SerialLink selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SerialLink record, @Param("example") SerialLinkExample example);

    int updateByExample(@Param("record") SerialLink record, @Param("example") SerialLinkExample example);

    int updateByPrimaryKeySelective(SerialLink record);

    int updateByPrimaryKey(SerialLink record);

    int batchInsert(@Param("list") List<SerialLink> list);

    int batchInsertSelective(@Param("list") List<SerialLink> list, @Param("selective") SerialLink.Column ... selective);
}