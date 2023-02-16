package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.HeraAppRole;
import com.xiaomi.mone.monitor.dao.model.HeraAppRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HeraAppRoleMapper {
    long countByExample(HeraAppRoleExample example);

    int deleteByExample(HeraAppRoleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HeraAppRole record);

    int insertSelective(HeraAppRole record);

    List<HeraAppRole> selectByExample(HeraAppRoleExample example);

    HeraAppRole selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HeraAppRole record, @Param("example") HeraAppRoleExample example);

    int updateByExample(@Param("record") HeraAppRole record, @Param("example") HeraAppRoleExample example);

    int updateByPrimaryKeySelective(HeraAppRole record);

    int updateByPrimaryKey(HeraAppRole record);

    int batchInsert(@Param("list") List<HeraAppRole> list);

    int batchInsertSelective(@Param("list") List<HeraAppRole> list, @Param("selective") HeraAppRole.Column ... selective);
}