package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.TestCaseGroup;
import com.xiaomi.miapi.common.pojo.TestCaseGroupExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestCaseGroupMapper {
    long countByExample(TestCaseGroupExample example);

    int deleteByExample(TestCaseGroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TestCaseGroup record);

    int insertSelective(TestCaseGroup record);

    List<TestCaseGroup> selectByExample(TestCaseGroupExample example);

    TestCaseGroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TestCaseGroup record, @Param("example") TestCaseGroupExample example);

    int updateByExample(@Param("record") TestCaseGroup record, @Param("example") TestCaseGroupExample example);

    int updateByPrimaryKeySelective(TestCaseGroup record);

    int updateByPrimaryKey(TestCaseGroup record);

    int batchInsert(@Param("list") List<TestCaseGroup> list);

    int batchInsertSelective(@Param("list") List<TestCaseGroup> list, @Param("selective") TestCaseGroup.Column ... selective);
}