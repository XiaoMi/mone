package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.Conf;
import com.xiaomi.data.push.dao.model.ConfExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConfMapper {
    int countByExample(ConfExample example);

    int deleteByExample(ConfExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Conf record);

    int insertSelective(Conf record);

    List<Conf> selectByExampleWithBLOBs(ConfExample example);

    List<Conf> selectByExample(ConfExample example);

    Conf selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Conf record, @Param("example") ConfExample example);

    int updateByExampleWithBLOBs(@Param("record") Conf record, @Param("example") ConfExample example);

    int updateByExample(@Param("record") Conf record, @Param("example") ConfExample example);

    int updateByPrimaryKeySelective(Conf record);

    int updateByPrimaryKeyWithBLOBs(Conf record);

    int updateByPrimaryKey(Conf record);
}