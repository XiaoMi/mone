package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogNumAlertDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MilogLogNumAlertMapper extends BaseMapper<MilogLogNumAlertDO> {
    void insertBatch(@Param(value = "doList") List<MilogLogNumAlertDO> doList);

    Long isSend(@Param(value = "appId") Long appId, @Param(value = "day") String day);

    void deleteThisDay(@Param(value = "day") String day);
}
