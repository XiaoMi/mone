package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.BusProjectGroup;
import com.xiaomi.miapi.pojo.BusProjectGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface BusProjectGroupMapper {
    long countByExample(BusProjectGroupExample example);

    int deleteByExample(BusProjectGroupExample example);

    int deleteByPrimaryKey(Integer groupId);

    int insert(BusProjectGroup record);

    int insertSelective(BusProjectGroup record);

    List<BusProjectGroup> selectByExample(BusProjectGroupExample example);

    BusProjectGroup selectByPrimaryKey(Integer groupId);

    int updateByExampleSelective(@Param("record") BusProjectGroup record, @Param("example") BusProjectGroupExample example);

    int updateByExample(@Param("record") BusProjectGroup record, @Param("example") BusProjectGroupExample example);

    int updateByPrimaryKeySelective(BusProjectGroup record);

    int updateByPrimaryKey(BusProjectGroup record);

    int batchInsert(@Param("list") List<BusProjectGroup> list);

    int batchInsertSelective(@Param("list") List<BusProjectGroup> list, @Param("selective") BusProjectGroup.Column ... selective);
}