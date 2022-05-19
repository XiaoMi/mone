package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.Mock;
import com.xiaomi.data.push.dao.model.MockExample;
import com.xiaomi.data.push.dao.model.MockWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MockMapper {
    int countByExample(MockExample example);

    int deleteByExample(MockExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MockWithBLOBs record);

    int insertSelective(MockWithBLOBs record);

    List<MockWithBLOBs> selectByExampleWithBLOBs(MockExample example);

    List<Mock> selectByExample(MockExample example);

    MockWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MockWithBLOBs record, @Param("example") MockExample example);

    int updateByExampleWithBLOBs(@Param("record") MockWithBLOBs record, @Param("example") MockExample example);

    int updateByExample(@Param("record") Mock record, @Param("example") MockExample example);

    int updateByPrimaryKeySelective(MockWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(MockWithBLOBs record);

    int updateByPrimaryKey(Mock record);
}