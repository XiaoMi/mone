package run.mone.m78.service.common;

import com.xiaomi.data.push.redis.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.m78.common.Constant;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author HawickMason@xiaomi.com
 * @date 5/29/24 17:08
 */
@Component
public class FlowTestResMapHolder {

    private static final int FLOW_TEST_RES_TTL = 1000 * 60 * 30;

    @Autowired
    private Redis redis;

    public static final ConcurrentMap<String, Boolean> FLOW_TEST_RES_MAP = new ConcurrentHashMap<>();

    /**
     * 获取指定流程记录ID的状态
     *
     * @param flowRecordId 流程记录ID
     * @param b            默认状态值
     * @return 流程记录的状态，如果Redis中没有对应的值，则返回默认状态值
     */
    public boolean getStatus(String flowRecordId, boolean b) {
        return Boolean.parseBoolean(Optional.ofNullable(redis.get(Constant.M78_FLOW_EXE_RES_PREFIX + flowRecordId)).orElse(String.valueOf(b)));
    }

    /**
     * 更新流程记录的状态
     *
     * @param flowRecordId 流程记录的ID
     * @param b            状态值，true或false
     */
    public void updateStatus(String flowRecordId, boolean b) {
        redis.setV2(Constant.M78_FLOW_EXE_RES_PREFIX + flowRecordId, String.valueOf(b), FLOW_TEST_RES_TTL);
    }
}
