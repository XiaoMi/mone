package com.xiaomi.mone.log.parse;

import lombok.*;

/**
 * @version 1.0
 * @Author wtt
 * @description
 * @date 2022/5/6 14:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class LogParserData {
    private String keyList;
    private String valueList;
    private String parseScript;
    private String topicName;
    private String tailName;
    private String mqTag;
    private String logStoreName;
}
