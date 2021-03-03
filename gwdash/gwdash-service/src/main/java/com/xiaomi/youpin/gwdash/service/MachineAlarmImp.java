/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.service;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.gwdash.dao.model.Machine;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangjunyi
 * created on 2020/3/11 11:18 上午
 */
@Service(interfaceClass = MachineAlarm.class, retries = 1, group = "${dubbo.group}")
public class MachineAlarmImp implements MachineAlarm {
    @Autowired
    Dao dao;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    private static String URL = "http://support.d.xiaomi.net/mail/send";
    private static int SEND_EMAILS_TIMES_MAX = 3;
    private int sendEmailsTimes = 0;

    /**
     *   mischedule 定时检查机器的状态
     *   machine 列表  更新时间和调度的时间差距大于10 分钟则标记为有问题的机器
     *   给组为架构组的人 发送邮件
     * @return
     *  Result<String>
     */
    @Override
    public Result<String> checkMachineByMischedule() {

        List<Machine> machines = dao.query(Machine.class, null);
        List<Machine> needNotifyMachines = new ArrayList<>();
        long now = System.currentTimeMillis();
        long tenMin = 3600 * 1000;
        needNotifyMachines = machines.stream().filter(machine -> now - machine.getUtime() > tenMin).collect(Collectors.toList());
        if (needNotifyMachines.size() == 0) {
            this.sendEmailsTimes = 0;
            return Result.success("无异常机器");
        }
        ;
        if (this.sendEmailsTimes > SEND_EMAILS_TIMES_MAX) {
            return Result.success("已经超过最大报警次数：" + SEND_EMAILS_TIMES_MAX);
        }

        StringBuffer content = new StringBuffer();
        content.append("<div style='font-size:18px'>");
        content.append("以下机器agent已经超过一小时无响应，请检查：<br>");
        needNotifyMachines.stream().forEach(machine -> {
            content.append("ID: " + machine.getId() + "    Name: " + machine.getName() + " " + "    Ip: " + machine.getIp() + "    My_group: " + machine.getGroup());
            content.append("<br>");
        });
        content.append("</div>");
        List<Account> accounts = accountService.queryUserByGroupName("业务架构组");
        List<String> emails = accounts.stream().map(account -> account.getEmail()).collect(Collectors.toList());
        this.send(String.join(";", emails), "miOne Machine disConnected", content.toString());
        this.sendEmailsTimes++;
        return Result.success(content.toString());

    }

    public static void send(String addressList, String title, String body) {
        String postBody = "title=" + title
                + "&body=" + body
                + "&address=" + addressList
                + "&locale=";

        String post = HttpClientV2.post(URL, postBody, Maps.newHashMap(), 5000);

    }
}