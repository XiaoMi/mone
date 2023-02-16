package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppServiceMarket;
import com.xiaomi.mone.monitor.dao.model.AppServiceMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppServiceMarketMapper {
    long countByExample(AppServiceMarketExample example);

    int deleteByExample(AppServiceMarketExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppServiceMarket record);

    int insertSelective(AppServiceMarket record);

    List<AppServiceMarket> selectByExample(AppServiceMarketExample example);

    AppServiceMarket selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppServiceMarket record, @Param("example") AppServiceMarketExample example);

    int updateByExample(@Param("record") AppServiceMarket record, @Param("example") AppServiceMarketExample example);

    int updateByPrimaryKeySelective(AppServiceMarket record);

    int updateByPrimaryKey(AppServiceMarket record);

    int batchInsert(@Param("list") List<AppServiceMarket> list);

    int batchInsertSelective(@Param("list") List<AppServiceMarket> list, @Param("selective") AppServiceMarket.Column ... selective);
}