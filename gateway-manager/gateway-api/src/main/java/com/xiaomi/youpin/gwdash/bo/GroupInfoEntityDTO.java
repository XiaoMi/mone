package com.xiaomi.youpin.gwdash.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jiangzheng
 * @version 1.0
 * @description:
 */
public class GroupInfoEntityDTO implements Serializable {

    private int id;

    private  String name;

    private  String description;

    private Date creationDate;

    private  Date modifyDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
