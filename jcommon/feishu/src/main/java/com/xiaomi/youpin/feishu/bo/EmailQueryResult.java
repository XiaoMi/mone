package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmailQueryResult {

    private Map<String,List<UserIdInfo>> email_users;

    private List<String> emails_not_exist;


}
