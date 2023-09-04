package com.xiaomi.youpin.prometheus.agent.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@ToString(callSuper = true)
@Table("silence_matcher")
@Data
public class RuleSilenceMatcherEntity {
    @Column("silence_id")
    private String silenceId;

    @Column("name")
    private String name;

    @Column("value")
    private String value;

    @Column("is_regex")
    private int isRegex;

    @Column("is_equal")
    private int isEqual;

}
