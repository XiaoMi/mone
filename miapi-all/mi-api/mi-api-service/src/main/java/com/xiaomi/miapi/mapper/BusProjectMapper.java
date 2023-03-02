package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.BusProject;
import com.xiaomi.miapi.pojo.BusProjectExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface BusProjectMapper {
    long countByExample(BusProjectExample example);

    int deleteByExample(BusProjectExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BusProject record);

    int insertSelective(BusProject record);

    List<BusProject> selectByExample(BusProjectExample example);

    BusProject selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BusProject record, @Param("example") BusProjectExample example);

    int updateByExample(@Param("record") BusProject record, @Param("example") BusProjectExample example);

    int updateByPrimaryKeySelective(BusProject record);

    int updateByPrimaryKey(BusProject record);

    int batchInsert(@Param("list") List<BusProject> list);

    int batchInsertSelective(@Param("list") List<BusProject> list, @Param("selective") BusProject.Column ... selective);
}