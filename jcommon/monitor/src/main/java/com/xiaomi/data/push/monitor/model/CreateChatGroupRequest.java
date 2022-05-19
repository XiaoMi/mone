package com.xiaomi.data.push.monitor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maojinrui
 */
@Data
public class CreateChatGroupRequest {

    private String name;
    private String owner;
    private List<String> userlist;

    public void addUser(String userName) {
        if (userlist == null) {
            userlist = new ArrayList<>();
            owner = userName;
        }
        userlist.add(userName);
    }
}