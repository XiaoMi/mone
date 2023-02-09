package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.SidecarPushData;
import com.xiaomi.miapi.pojo.SidecarPushDataExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface SidecarPushDataMapper {
    long countByExample(SidecarPushDataExample example);

    int deleteByExample(SidecarPushDataExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SidecarPushData record);

    int insertSelective(SidecarPushData record);

    List<SidecarPushData> selectByExampleWithBLOBs(SidecarPushDataExample example);

    List<SidecarPushData> selectByExample(SidecarPushDataExample example);

    SidecarPushData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SidecarPushData record, @Param("example") SidecarPushDataExample example);

    int updateByExampleWithBLOBs(@Param("record") SidecarPushData record, @Param("example") SidecarPushDataExample example);

    int updateByExample(@Param("record") SidecarPushData record, @Param("example") SidecarPushDataExample example);

    int updateByPrimaryKeySelective(SidecarPushData record);

    int updateByPrimaryKeyWithBLOBs(SidecarPushData record);

    int updateByPrimaryKey(SidecarPushData record);

    int batchInsert(@Param("list") List<SidecarPushData> list);

    int batchInsertSelective(@Param("list") List<SidecarPushData> list, @Param("selective") SidecarPushData.Column ... selective);
}