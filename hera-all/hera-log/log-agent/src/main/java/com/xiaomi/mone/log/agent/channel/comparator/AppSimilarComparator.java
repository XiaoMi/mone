package com.xiaomi.mone.log.agent.channel.comparator;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/15 10:04
 */
public class AppSimilarComparator implements SimilarComparator<Long> {

    private Long oldAppId;

    public AppSimilarComparator(Long oldAppId) {
        this.oldAppId = oldAppId;
    }

    @Override
    public boolean compare(Long newAppId) {
        return Long.compare(oldAppId, newAppId) == 0;
    }
}
