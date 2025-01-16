package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import run.mone.m78.service.dao.entity.M78BotComment;
import run.mone.m78.service.dao.entity.M78BotCommentLevel;
import run.mone.m78.service.dao.entity.M78BotCommentStatistics;

import java.util.List;

/**
 *  映射层。
 *
 * @author hoho
 * @since 2024-03-04
 */
public interface M78BotCommentMapper extends BaseMapper<M78BotComment> {

    @Select("select item_id as botId, count(1) as totalCount from m78_bot_comment where item_id = #{itemId} and type = #{type}")
    M78BotCommentStatistics getTotalAndAverageByBotId(@Param("itemId") Long itemId, @Param("type") int type);

    @Select("select count(1) as count, score from  m78_bot_comment where item_id = #{itemId} and type = #{type} group by score")
    List<M78BotCommentLevel> getCommentLevelCount(@Param("itemId") Long itemId, @Param("type") int type);

    @Update("update m78_bot set bot_avg_star = (select ROUND(AVG(score), 1) from m78_bot_comment where item_id = #{itemId} and type = 0) where id = #{itemId}")
    int updateAvgScore(@Param("itemId") Long itemId);
}
