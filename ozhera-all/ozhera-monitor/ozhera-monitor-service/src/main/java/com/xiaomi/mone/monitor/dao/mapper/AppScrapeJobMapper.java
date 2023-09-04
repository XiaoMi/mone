package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppScrapeJob;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppScrapeJobMapper {
    long countByExample(AppScrapeJobExample example);

    int deleteByExample(AppScrapeJobExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppScrapeJob record);

    int insertSelective(AppScrapeJob record);

    List<AppScrapeJob> selectByExampleWithBLOBs(AppScrapeJobExample example);

    List<AppScrapeJob> selectByExample(AppScrapeJobExample example);

    AppScrapeJob selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppScrapeJob record, @Param("example") AppScrapeJobExample example);

    int updateByExampleWithBLOBs(@Param("record") AppScrapeJob record, @Param("example") AppScrapeJobExample example);

    int updateByExample(@Param("record") AppScrapeJob record, @Param("example") AppScrapeJobExample example);

    int updateByPrimaryKeySelective(AppScrapeJob record);

    int updateByPrimaryKeyWithBLOBs(AppScrapeJob record);

    int updateByPrimaryKey(AppScrapeJob record);

    int batchInsert(@Param("list") List<AppScrapeJob> list);

    int batchInsertSelective(@Param("list") List<AppScrapeJob> list, @Param("selective") AppScrapeJob.Column ... selective);
}