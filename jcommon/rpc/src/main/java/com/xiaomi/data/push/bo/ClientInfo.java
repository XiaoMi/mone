package com.xiaomi.data.push.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: goodjava
 * Date: 2020/5/24
 * Time: 12:32 PM
 */
@Data
@AllArgsConstructor
public class ClientInfo {

    private String name;
    private String ip;
    private int port;
    private String version;
}
