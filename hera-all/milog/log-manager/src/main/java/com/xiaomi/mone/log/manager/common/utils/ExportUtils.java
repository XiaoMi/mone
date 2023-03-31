package com.xiaomi.mone.log.manager.common.utils;

import com.xiaomi.mone.log.manager.model.dto.LogDataDTO;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 日志下载工具类
 */
public class ExportUtils {

    // HSSFWorkbook4Map 方法写 xls 有最大单元格长度限制 32767，对于超长的内容做分割
    public static Map<String, Object> SplitTooLongContent(LogDataDTO logDataDto) {
        int maxCellLen = 32767;
        Map<String, Object> logOfKV = logDataDto.getLogOfKV();
        Map<String, Object> newLogOfKV = new LinkedHashMap<>();
        Iterator<Map.Entry<String, Object>> it = logOfKV.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String keyStr = entry.getKey();
            String valueStr = entry.getValue() == null ? "" : entry.getValue().toString();
            int entryLen = valueStr.length();
            if (entryLen > maxCellLen) {
                int cnt = entryLen / maxCellLen;
                if (entryLen % maxCellLen != 0) {
                    cnt++;
                }
                for (int i = 0; i < cnt; i++) {
                    String entryKey = String.format("%s-%d", keyStr, i);
                    String entryValue = "";
                    int end = (i + 1) * maxCellLen;
                    if (end > entryLen) {
                        end = entryLen;
                    }
                    entryValue = valueStr.substring(i * maxCellLen, end);
                    newLogOfKV.put(entryKey, entryValue);
                }
            } else {
                newLogOfKV.put(keyStr, entry.getValue());
            }
        }
        return newLogOfKV;
    }
}
