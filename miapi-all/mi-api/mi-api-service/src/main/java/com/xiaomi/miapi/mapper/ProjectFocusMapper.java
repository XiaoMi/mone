package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ProjectFocus;
import com.xiaomi.miapi.pojo.ProjectFocusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ProjectFocusMapper {
    long countByExample(ProjectFocusExample example);

    int deleteByExample(ProjectFocusExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProjectFocus record);

    int insertSelective(ProjectFocus record);

    List<ProjectFocus> selectByExample(ProjectFocusExample example);

    ProjectFocus selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProjectFocus record, @Param("example") ProjectFocusExample example);

    int updateByExample(@Param("record") ProjectFocus record, @Param("example") ProjectFocusExample example);

    int updateByPrimaryKeySelective(ProjectFocus record);

    int updateByPrimaryKey(ProjectFocus record);

    int batchInsert(@Param("list") List<ProjectFocus> list);

    int batchInsertSelective(@Param("list") List<ProjectFocus> list, @Param("selective") ProjectFocus.Column ... selective);
}