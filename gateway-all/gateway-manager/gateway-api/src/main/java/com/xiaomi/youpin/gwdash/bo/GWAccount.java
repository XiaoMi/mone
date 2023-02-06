package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class GWAccount  implements Serializable {

    private Long id;

    private String userName;

    private String gid;

    private String email;

    private String phone;

    private Long ctime;

    private Long utime;

    private List<GroupInfoEntity> gidInfos = new ArrayList<>();

}
