package com.xiaomi.mone.app.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.app.model.HeraAppRole;
import com.xiaomi.mone.app.model.HeraAppRoleExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface HeraAppRoleMapper extends BaseMapper<HeraAppRole> {
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

    int batchInsertSelective(@Param("list") List<HeraAppRole> list, @Param("selective") HeraAppRole.Column... selective);
}