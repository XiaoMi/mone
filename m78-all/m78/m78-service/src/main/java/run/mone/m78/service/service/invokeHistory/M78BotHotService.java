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
package run.mone.m78.service.service.invokeHistory;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.xiaomi.data.push.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.mone.m78.service.bo.bot.BotBo;
import run.mone.m78.service.dao.entity.M78Bot;
import run.mone.m78.service.dao.entity.M78InvokeSummaryPerdayPo;
import run.mone.m78.service.dao.mapper.M78BotMapper;
import run.mone.m78.service.dao.mapper.M78InvokeSummaryPerdayMapper;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static run.mone.m78.api.bo.invokeHistory.InvokeTypeEnum.PROBOT;

@Service
@Slf4j

/**
 * M78BotHotService类负责管理和统计机器人（bot）的使用情况和热度。
 * <p>
 * 该类提供了以下主要功能：
 * <ul>
 *     <li>每天定时初始化，统计前一天的机器人使用情况。</li>
 *     <li>计算机器人在过去7天内的热度值。</li>
 *     <li>更新机器人使用次数。</li>
 * </ul>
 * <p>
 * 该类依赖于Redis进行分布式锁的管理和数据缓存，依赖于数据库Mapper进行数据的查询和更新。
 * <p>
 * 主要方法：
 * <ul>
 *     <li>{@link #init()} - 每天零点45分触发的初始化方法。</li>
 *     <li>{@link #summarizeBotHot()} - 统计前一天的机器人使用情况。</li>
 *     <li>{@link #calculateBotHot(Long)} - 根据给定的botId计算该bot在过去7天的热度值。</li>
 *     <li>{@link #adapterHotForProbot(List)} - 更新机器人使用次数。</li>
 * </ul>
 * <p>
 * 该类使用了Spring的@Service注解标识为服务类，并使用@Slf4j注解启用日志记录功能。
 */

public class M78BotHotService {

    private static final String LOCK_KEY = "M78BotHotServiceLock";

    @Autowired
    private Redis redis;

    @Resource
    private M78BotMapper botMapper;

    @Resource
    private M78InvokeSummaryPerdayMapper m78InvokeSummaryPerdayMapper;


    /**
     * 每天零点45分触发的初始化方法
     * <p>
     * 该方法在每天的00:45被定时调用。首先尝试获取Redis锁，如果获取失败，则记录日志并返回。
     * 如果成功获取锁，则记录初始化触发的日志，并统计前一天的调用情况。
     *
     * @throws Exception 可能在统计调用情况时抛出异常
     */
    //写一个init方法，每天零点45分的时候触发调用
    @Scheduled(cron = "0 45 0 * * ?")
//    @PostConstruct
    public void init() {
        if (!RedisUtils.acquireLock(redis, LOCK_KEY)) {
            log.info("ignore at 00:05");
            return;
        }
        log.info("Init method triggered at 00:05 every day");

        //统计前一天的调用情况
        try {
            summarizeBotHot();
        } catch (Exception e) {
            log.error("[M78BotHotService], failed to summarizeBotHot, error: {}", e.getMessage());
        }
    }

    private void summarizeBotHot() {
        List<M78Bot> bots = botMapper.selectListByQuery(new QueryWrapper().eq("deleted", 0));
        for (M78Bot bot : bots) {
            try {
                // 改表
                M78Bot m78BotUpdate = UpdateEntity.of(M78Bot.class, bot.getId());
                m78BotUpdate.setBotUseTimes(calculateBotHot(bot.getId()));
                botMapper.insertOrUpdateSelective(m78BotUpdate);
            } catch (Throwable t) {
                log.error("update hot count to mysql error, ", t);
            }
        }
    }

    /**
     * 根据给定的botId计算该bot在过去7天的热度值
     * <p>
     * 该方法会查询与botId相关的InvokeSummaryPerday调用数据，并根据
     * 不同日期的数据应用不同的权重进行加权计算，最后返回向上取整的热度值。
     * 权重分配如下：
     * 1天前的数据 * 1.0
     * 2天前的数据 * 0.8
     * 3天前的数据 * 0.5
     * 4天前的数据 * 0.4
     * 5天前的数据 * 0.3
     * 6天前的数据 * 0.2
     * 7天前的数据 * 0.1
     *
     * @param botId 需要计算热度的bot的唯一标识
     * @return 计算得到的热度值，向上取整后的结果
     */
    //按botid查找前7天的InvokeSummaryPerday调用数据，按比例生成最后的热度，7天前的数据*0.1，6天前的数据*0.2，5天前的数据*0.3，4天前的数据*0.4，3天前的数据*0.5，2天前的数据*0.8，1天前的数据*1，最终加和，向上取整
    public long calculateBotHot(Long botId) {
        List<M78InvokeSummaryPerdayPo> summaries = m78InvokeSummaryPerdayMapper.selectListByQuery(
                new QueryWrapper()
                        .eq("relate_id", botId)
                        .eq("type", PROBOT.getCode())
                        .orderBy("invoke_day", false)
                        .limit(7)
        );

        double[] weights = {1.0, 0.8, 0.5, 0.4, 0.3, 0.2, 0.1};
        double hotScore = 0;

        for (int i = 0; i < summaries.size(); i++) {
            hotScore += summaries.get(i).getInvokeCounts() * weights[i];
        }

        return (long) Math.ceil(hotScore);
    }

    /**
     * 更新机器人使用次数
     *
     * @param boList 需要更新的机器人列表
     */

    public void adapterHotForProbot(List<BotBo> boList) {
        List<String> redisKeys = getRedisKeys(boList);
        Map<String, String> mget = redis.mget(redisKeys);
        log.info("get use tims redis mget keys : " + redisKeys + " result : " + mget);
        for (BotBo botBo : boList) {
            String redisCount = mget.get(RedisUtils.getProbotHotKey(botBo.getId()));
            if (redisCount != null && !"null".equals(redisCount)) {
                try {
                    botBo.setBotUseTimes(Long.parseLong(redisCount) + botBo.getBotUseTimes());
                } catch (Exception e) {
                    botBo.setBotUseTimes(0L);
                    log.error("bot use times parse error:{}", redisCount, e);
                }
            }
        }
    }

    private List<String> getRedisKeys(List<BotBo> boList) {
        return boList.stream().map(it -> RedisUtils.getProbotHotKey(it.getId())).collect(Collectors.toList());
    }


}
