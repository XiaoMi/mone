package com.xiaomi.mone.file;

import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/8 15:57
 */
@Data
public class MLog {

    @Getter
    private Queue<String> msgQueue = new LinkedList<>();
    @Getter
    private Long appendTime;

    /**
     * 匹配 20xx or [20xx
     */
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("^20[0-9]{2}|^\\[20[0-9]{2}");
//    private static final Pattern ERROR_LINE_PATTERN = Pattern.compile(".*ERROR|.*(WARN|INFO).*(Exception|exception|error|Profiler)");
//    private static final int MAX_ERROR_LINE_MATCH_LENGTH = 300;
    /**
     * 最多聚合200行错误栈，避免queue无限增长
     */
    private static final int MAX_MERGE_LINE = 200;
    private byte[] lock = new byte[0];

    public List<String> append(String msg) {
        appendTime = Instant.now().toEpochMilli();
        List<String> res = new ArrayList<>();
        boolean isNew = isNew(msg);
        if (isNew) {
            synchronized (lock) {
                if (msgQueue.size() > 0) {
                    String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    res.add(e);
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            }
        } else {
            synchronized (lock) {
                //最大错误栈行数判断
                if (msgQueue.size() >= MAX_MERGE_LINE) {
                    String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    res.add(e);
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            }
        }

        return res;
    }

    /**
     * 获取队列中剩余消息
     * @return
     */
    public List<String> takeRemainMsg() {
        List<String> res = new ArrayList<>();
        synchronized (lock) {
            if (msgQueue.size() > 0) {
                String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                res.add(e);
                msgQueue.clear();
            }
        }

        return res;
    }

    private boolean isNew(String str) {
        return NEW_LINE_PATTERN.matcher(str).find();
    }

}
