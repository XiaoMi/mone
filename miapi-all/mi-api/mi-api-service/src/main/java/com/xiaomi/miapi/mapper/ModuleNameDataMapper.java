package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ModuleNameData;
import com.xiaomi.miapi.pojo.ModuleNameDataExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ModuleNameDataMapper {
    long countByExample(ModuleNameDataExample example);

    int deleteByExample(ModuleNameDataExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ModuleNameData record);

    int insertSelective(ModuleNameData record);

    List<ModuleNameData> selectByExample(ModuleNameDataExample example);

    ModuleNameData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ModuleNameData record, @Param("example") ModuleNameDataExample example);

    int updateByExample(@Param("record") ModuleNameData record, @Param("example") ModuleNameDataExample example);

    int updateByPrimaryKeySelective(ModuleNameData record);

    int updateByPrimaryKey(ModuleNameData record);

    int batchInsert(@Param("list") List<ModuleNameData> list);

    int batchInsertSelective(@Param("list") List<ModuleNameData> list, @Param("selective") ModuleNameData.Column ... selective);
}