package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author jiangzheng
 * created On 2022-01-18 15:54
 */
@Data
public class GroupInfoEntity implements Serializable {

    private int id;

    private  String name;

    private  String description;

    private Date creationDate;

    private  Date modifyDate;

}
