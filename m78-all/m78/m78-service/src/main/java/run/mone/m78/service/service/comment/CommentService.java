/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.service.service.comment;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.common.Constant;
import run.mone.m78.service.dao.entity.M78Bot;
import run.mone.m78.service.dao.entity.M78BotComment;
import run.mone.m78.service.dao.entity.M78BotCommentLevel;
import run.mone.m78.service.dao.entity.M78BotCommentStatistics;
import run.mone.m78.service.dao.entity.M78BotPluginOrg;
import run.mone.m78.service.dao.mapper.M78BotCommentMapper;
import run.mone.m78.service.dao.mapper.M78BotMapper;
import run.mone.m78.service.dao.mapper.M78BotPluginOrgMapper;
import run.mone.m78.service.dao.mapper.M78FlowBaseMapper;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j

/**
 * CommentService类提供了对评论的插入、更新、查询和统计功能。
 * 该类使用了一个线程池来处理异步任务，以提高性能。
 *
 * 主要功能包括：
 * - 插入或更新评论，并在成功后异步更新相关表的评论分字段。
 * - 根据机器人ID获取评论分页结果。
 * - 根据机器人ID和用户信息查询评论。
 * - 获取指定项的评论统计信息。
 *
 * 该类依赖于多个Mapper类来执行数据库操作，并使用了Spring的@Autowired注解进行依赖注入。
 *
 * 注：该类使用了@Slf4j注解来记录日志信息。
 */

public class CommentService {

    private static BlockingQueue<Runnable> commentQueue = new ArrayBlockingQueue<>(10);

    private static AtomicInteger threadNumber = new AtomicInteger(1);

    private ThreadPoolExecutor CommentThreadPool = new ThreadPoolExecutor(20, 40,
            0L, TimeUnit.MILLISECONDS,
            commentQueue, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("comment-statistics-" + threadNumber.getAndIncrement());
            return thread;
        }
    });

    @Autowired
    private M78BotCommentMapper commentMapper;
    @Autowired
    private M78BotMapper botMapper;
    @Autowired
    private M78BotPluginOrgMapper botPluginOrgMapper;
    @Autowired
    private M78FlowBaseMapper m78FlowBaseMapper;

    /**
     * 插入或更新评论
     *
     * @param comment 评论对象
     * @return 操作结果
     */
    public Result insertOrUpdate(M78BotComment comment) {
        try {
            if (comment.getId() != null) {
                // 校验只能修改他自己的评论
                M78BotComment commentOld = commentMapper.selectOneById(comment.getId());
                if (commentOld == null) {
                    log.error("update comment is null, comment : " + comment);
                    return Result.fail(GeneralCodes.InternalError, "update comment is null");
                } else {
                    if (!commentOld.getCreateBy().equals(comment.getUpdateBy())) {
                        log.error("update comment exceed authority, comment : " + comment);
                        return Result.fail(GeneralCodes.InternalError, "update comment exceed authority");
                    }
                }
            } else {
                // 校验该用户是否已经评论过该botId
                QueryWrapper qw = new QueryWrapper();
                qw.eq("item_id", comment.getItemId());
                qw.eq("type", comment.getType());
                qw.eq("create_by", comment.getCreateBy());
                if (commentMapper.selectOneByQuery(qw) != null) {
                    log.error("insert comment repeat, comment : " + comment);
                    return Result.fail(GeneralCodes.InternalError, "Users have already commented on the bot");
                }
            }
            int i = commentMapper.insertOrUpdateSelective(comment);
            if (i > 0) {
                // 新增或修改成功后，异步更新bot表的评论分字段
                if (Constant.BOT_COMMENT_TYPE == comment.getType()) {
                    CommentThreadPool.submit(() -> {
                        commentMapper.updateAvgScore(comment.getItemId());
                    });
                } else if (Constant.PLUGIN_COMMENT_TYPE == comment.getType()) {
                    CommentThreadPool.submit(() -> {
                        botPluginOrgMapper.updateAvgScore(comment.getItemId());
                    });
                } else if (Constant.FLOW_COMMENT_TYPE == comment.getType()) {
                    CommentThreadPool.submit(() -> {
                        m78FlowBaseMapper.updateAvgScore(comment.getItemId());
                    });
                }
                return Result.success(null);
            } else {
                log.error("insert or update bot comment effect rows is 0 , comment : " + comment);
            }
        } catch (Throwable t) {
            log.error("insert bot comment error , comment : " + comment, t);
        }
        return Result.fail(GeneralCodes.InternalError, "insert or update error");
    }

    /**
     * 根据机器人ID获取评论分页结果
     *
     * @param itemId      机器人ID，如果为null则根据创建者查询
     * @param type        评论类型
     * @param user        创建者用户名
     * @param commentType 评论的具体类型（好评、中评、差评）
     * @param pageSize    每页显示的评论数量
     * @param pageNum     当前页码
     * @return 包含评论分页结果的Result对象，如果查询失败则返回错误信息
     */
    public Result<Page<M78BotComment>> getByBotId(Long itemId, int type, String user, String commentType, int pageSize, int pageNum) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (itemId == null) {
            queryWrapper.eq("create_by", user);
        } else {
            queryWrapper.eq("item_id", itemId);
            queryWrapper.eq("type", type);
        }
        if (commentType != null) {
            if (Constant.BAD_COMMENT_TYPE.equals(commentType)) {
                queryWrapper.le("score", 3);
            } else if (Constant.MEDIUM_COMMENT_TYPE.equals(commentType)) {
                queryWrapper.eq("score", 3);
            } else if (Constant.GOOD_COMMENT_TYPE.equals(commentType)) {
                queryWrapper.ge("score", 3);
            }
        }
        try {
            Page<M78BotComment> paginate = commentMapper.paginate(pageNum, pageSize, queryWrapper);
            return Result.success(paginate);
        } catch (Throwable t) {
            log.error("query bot comment by bot id error , item id is : " + itemId);
        }
        return Result.fail(GeneralCodes.InternalError, "query error");
    }

    /**
     * 根据机器人ID和用户信息查询评论
     *
     * @param itemId 机器人ID
     * @param type   评论类型
     * @param user   用户名
     * @return 查询结果，成功时返回包含评论的Result对象，失败时返回错误信息
     */
    public Result<M78BotComment> getByBotIdAndUser(Long itemId, int type, String user) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("create_by", user);
        queryWrapper.eq("item_id", itemId);
        queryWrapper.eq("type", type);
        try {
            M78BotComment comment = commentMapper.selectOneByQuery(queryWrapper);
            return Result.success(comment);
        } catch (Throwable t) {
            log.error("query bot comment by bot id and user error , item id is : " + itemId + ", user is : " + user);
        }
        return Result.fail(GeneralCodes.InternalError, "query error");
    }

    /**
     * 获取指定项的评论统计信息
     *
     * @param itemId 项目ID
     * @param type   评论类型
     * @return 包含评论统计信息的Result对象
     */
    public Result<M78BotCommentStatistics> statistics(Long itemId, int type) {
        try {
            CompletableFuture<M78BotCommentStatistics> statisticsFuture = CompletableFuture.supplyAsync(() -> commentMapper.getTotalAndAverageByBotId(itemId, type), CommentThreadPool);
            CompletableFuture<List<M78BotCommentLevel>> commentLevelCount = CompletableFuture.supplyAsync(() -> commentMapper.getCommentLevelCount(itemId, type), CommentThreadPool);
            CompletableFuture m78BotCompletableFuture = null;
            if (Constant.BOT_COMMENT_TYPE == type) {
                m78BotCompletableFuture = CompletableFuture.supplyAsync(() -> botMapper.selectOneById(itemId), CommentThreadPool);
            } else if (Constant.PLUGIN_COMMENT_TYPE == type) {
                m78BotCompletableFuture = CompletableFuture.supplyAsync(() -> botPluginOrgMapper.selectOneById(itemId), CommentThreadPool);
            }
            CompletableFuture<Void> allComplete = CompletableFuture.allOf(statisticsFuture, commentLevelCount);
            allComplete.get(8000, TimeUnit.MILLISECONDS);
            List<M78BotCommentLevel> m78BotCommentLevels = commentLevelCount.get();
            M78BotCommentStatistics statistics = statisticsFuture.get();
            double avgStar = 0;
            if (Constant.BOT_COMMENT_TYPE == type) {
                M78Bot m78Bot = (M78Bot) m78BotCompletableFuture.get();
                avgStar = m78Bot.getBotAvgStar();
            } else if (Constant.PLUGIN_COMMENT_TYPE == type) {
                M78BotPluginOrg org = (M78BotPluginOrg) m78BotCompletableFuture.get();
                avgStar = org.getPluginAvgStar();
            }
            setLevelCount(m78BotCommentLevels, statistics, avgStar);
            return Result.success(statistics);
        } catch (Throwable t) {
            log.error("statistics error : ", t);
        }
        return Result.fail(GeneralCodes.InternalError, "statistics error");
    }

    private void setLevelCount(List<M78BotCommentLevel> commentLevelCount, M78BotCommentStatistics statistics, double botAvgStar) {
        int badCount = 0, mediumCount = 0, goodCount = 0;
        for (M78BotCommentLevel level : commentLevelCount) {
            if (1 == level.getScore()) {
                statistics.setScore1(level.getCount());
                badCount += level.getCount();
            }
            if (2 == level.getScore()) {
                statistics.setScore2(level.getCount());
                badCount += level.getCount();
            }
            if (3 == level.getScore()) {
                statistics.setScore3(level.getCount());
                mediumCount += level.getCount();
            }
            if (4 == level.getScore()) {
                statistics.setScore4(level.getCount());
                goodCount += level.getCount();
            }
            if (5 == level.getScore()) {
                statistics.setScore5(level.getCount());
                goodCount += level.getCount();
            }
        }
        statistics.setBadCount(badCount);
        statistics.setMediumCount(mediumCount);
        statistics.setGoodCount(goodCount);
        statistics.setAverageScore(botAvgStar);
    }


}
