package com.xiaomi.mone.log.manager.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_TAGS;
import static com.xiaomi.mone.log.common.Constant.UNDERLINE_SYMBOL;

@Slf4j
public class Utils {

    public static List<String> parseMilogKeyListFromKeyAndType(String keyAndType) {
        String[] keyAndTypelist = keyAndType.split(",");
        List<String> keylist = new ArrayList<>();
        for (int i = 0; i < keyAndTypelist.length; i++) {
            String[] split = keyAndTypelist[i].split(":");
            if (split.length > 0) {
                keylist.add(split[0]);
            }
        }
        return keylist;
    }

    public static String parseMilogKeyListStrFromKeyAndType(String keyAndType) {
        String[] keyAndTypelist = keyAndType.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyAndTypelist.length; i++) {
            String[] split = keyAndTypelist[i].split(":");
            if (i != keyAndTypelist.length - 1 && split.length > 0) {
                sb.append(split[0]).append(",");
            } else if (i == keyAndTypelist.length - 1 && split.length > 0) {
                sb.append(split[0]);
            }
        }
        return sb.toString();
    }

    public static String parse2KeyAndTypeList(String KeyList, String TypeList) {
        String[] KLSplit = KeyList.split(",");
        String[] TLSplit = TypeList.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < KLSplit.length; i++) {
            String[] split = KLSplit[i].split(":");
            if (split.length > 0) {
                if (i != KLSplit.length - 1) {
                    sb.append(split[0]).append(":").append(TLSplit[i]).append(",");
                } else {
                    sb.append(split[0]).append(":").append(TLSplit[i]);
                }
            }
        }
        return sb.toString();
    }

    public static List<String> getKeyListSlice(String keyList) {
        String[] KLSplit = keyList.split(",");
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < KLSplit.length; i++) {
            String[] split = KLSplit[i].split(":");
            if (split.length > 1 && !split[1].equals("3")) {
                ret.add(split[0]);
            }
        }
        return ret;
    }

    /**
     * 简单的创建tag
     *
     * @param spaceId
     * @param storeId
     * @param tailId
     * @return
     */
    public static String createTag(Long spaceId, Long storeId, Long tailId) {
        return DEFAULT_TAGS + spaceId + UNDERLINE_SYMBOL + storeId + UNDERLINE_SYMBOL + tailId;
    }

    /**
     * 简单的组装一个topic名称
     *
     * @param appId
     * @param appName
     * @return
     */
    public static String assembleTopicName(Long appId, String appName) {
        StringBuilder sb = new StringBuilder(appName);
        sb.append("-");
        sb.append(appId);
        sb.append("-");
        sb.append("topic");
        return sb.toString();
    }

    public static String getKeyValueList(String keyList, String valueList) {
        List<String> keyListSlice = Utils.getKeyListSlice(keyList);
        String[] valueS = StringUtils.split(valueList, ",");
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < valueS.length; i++) {
            try {
                int orderValue = Integer.parseInt(valueS[i]);
                if (orderValue >= 0) {
                    map.put(orderValue, keyListSlice.get(i));
                }
            } catch (Exception e) {
                log.error(String.format("数据解析异常，keyList:%s,valueList:%s", keyList, valueS), e);
            }
        }
        return map.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.joining(","));
    }

    public static Map<String, Long> getTodayTime() {
        Map<String, Long> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long start = calendar.getTime().getTime();
        map.put("start", start);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        long end = calendar1.getTime().getTime();
        map.put("end", end);
        return map;
    }

    public static Integer getRandomNum(Integer right) {
        return ThreadLocalRandom.current().nextInt(right);
    }
}
