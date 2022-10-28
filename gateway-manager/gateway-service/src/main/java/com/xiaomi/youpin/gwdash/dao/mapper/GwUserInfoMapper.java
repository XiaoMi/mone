package com.xiaomi.youpin.gwdash.dao.mapper;

import com.xiaomi.youpin.gwdash.dao.model.GwUserInfo;
import org.apache.ibatis.annotations.Param;
import com.xiaomi.youpin.gwdash.bo.GWAccount;

public interface GwUserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GwUserInfo record);

    int insertWithId(GwUserInfo record);

    int insertSelective(GwUserInfo record);

    GwUserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GwUserInfo record);

    int updateByPrimaryKey(GwUserInfo record);

    // GwUserInfo queryUserByName(@Param("userName") String name);

    GwUserInfo queryUserByName2(@Param("userName") String name, @Param("tenant") String tenant);

    GWAccount queryAccountByName(@Param("userName") String name);

    GWAccount queryAccountByName(@Param("userName") String name, @Param("tenant") String tenant);

    GWAccount queryAccountById(@Param("id") int id);

}