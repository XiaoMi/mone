package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.Script;
import com.xiaomi.data.push.dao.model.ScriptExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScriptMapper {
    int countByExample(ScriptExample example);

    int deleteByExample(ScriptExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Script record);

    int insertSelective(Script record);

    List<Script> selectByExampleWithBLOBs(ScriptExample example);

    List<Script> selectByExample(ScriptExample example);

    Script selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByExampleWithBLOBs(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByExample(@Param("record") Script record, @Param("example") ScriptExample example);

    int updateByPrimaryKeySelective(Script record);

    int updateByPrimaryKeyWithBLOBs(Script record);

    int updateByPrimaryKey(Script record);
}