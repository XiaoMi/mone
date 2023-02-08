package com.xiaomi.youpin.docean.plugin.mybatis.transaction;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author goodjava@qq.com
 * @date 2022/12/1 10:13
 */
@Data
public class Xid implements Serializable {

    private String id;

    private String name;

    private long timeout;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Xid xid = (Xid) o;
        return Objects.equals(id, xid.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
