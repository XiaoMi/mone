package com.xiaomi.mone.tpc.dao.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 9:39
 */
@ToString(callSuper = true)
@Table("user_entity")
@Data
public class UserEntity extends BaseEntity{

    @Column
    private String account;

    public UserEntity updateForContent(String content) {
        UserEntity entity = new UserEntity();
        entity.setId(this.getId());
        entity.setType(this.getType());
        entity.setAccount(this.getAccount());
        entity.setContent(content);
        return entity;
    }


}
