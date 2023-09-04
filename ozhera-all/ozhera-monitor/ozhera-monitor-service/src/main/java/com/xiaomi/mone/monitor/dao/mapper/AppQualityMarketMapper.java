package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppQualityMarket;
import com.xiaomi.mone.monitor.dao.model.AppQualityMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppQualityMarketMapper {
    long countByExample(AppQualityMarketExample example);

    int deleteByExample(AppQualityMarketExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppQualityMarket record);

    int insertSelective(AppQualityMarket record);

    List<AppQualityMarket> selectByExample(AppQualityMarketExample example);

    AppQualityMarket selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppQualityMarket record, @Param("example") AppQualityMarketExample example);

    int updateByExample(@Param("record") AppQualityMarket record, @Param("example") AppQualityMarketExample example);

    int updateByPrimaryKeySelective(AppQualityMarket record);

    int updateByPrimaryKey(AppQualityMarket record);

    int batchInsert(@Param("list") List<AppQualityMarket> list);

    int batchInsertSelective(@Param("list") List<AppQualityMarket> list, @Param("selective") AppQualityMarket.Column ... selective);
}