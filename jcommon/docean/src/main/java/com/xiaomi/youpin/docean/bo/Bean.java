package com.xiaomi.youpin.docean.bo;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Data
public class Bean implements Comparable<Bean>, Serializable {

    private String name;

    private String alias;

    private String lookup;

    private Class<?> clazz;

    private transient Object obj;

    private int type;

    /**
     * 被引用的次数
     */
    private int referenceCnt;

    private List<String> dependenceList = new ArrayList<>();

    private Map<String,String> attachments = Maps.newHashMap();

    public void incrReferenceCnt() {
        this.referenceCnt++;
    }

    @Override
    public int compareTo(Bean o) {
        if (this.type < o.getType()) {
            return -1;
        }
        if (this.type > o.getType()) {
            return 1;
        }

        if (this.type == o.type) {
            if (this.referenceCnt < o.referenceCnt) {
                return -1;
            }
            if (this.referenceCnt > o.referenceCnt) {
                return 1;
            }
        }
        return 0;
    }


    public enum Type {
        controller, service, component
    }

}
