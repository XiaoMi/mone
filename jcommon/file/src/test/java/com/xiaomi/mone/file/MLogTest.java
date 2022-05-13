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

package com.xiaomi.mone.file;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/9 11:33
 */
public class MLogTest {
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("^20[0-9]{2}");
    private static final Pattern ERROR_LINE_PATTERN = Pattern.compile(".*ERROR|.*(WARN|INFO).*Exception");

    @Test
    public void test1() {
        MLog log = new MLog();
        System.out.println(log.append("2021|ERROR"));
        System.out.println(log.append("error"));
        System.out.println(log.append("error2"));
        System.out.println(log.append("2021 2|ERROR"));
        System.out.println(log.append("error a"));
        System.out.println(log.append("error b"));
        System.out.println(log.append("error c"));
        System.out.println(log.append("2021 4|INFO"));
        System.out.println(log.append("2021 5|INFO"));
    }

    @Test
    public void test2() {
        System.out.println(NEW_LINE_PATTERN.matcher("2021").find());
        System.out.println(NEW_LINE_PATTERN.matcher("2022").find());
        System.out.println(NEW_LINE_PATTERN.matcher("20225").find());
        System.out.println(NEW_LINE_PATTERN.matcher("201x").find());
        System.out.println(NEW_LINE_PATTERN.matcher("2112").find());
        System.out.println(NEW_LINE_PATTERN.matcher("1012").find());
        System.out.println(NEW_LINE_PATTERN.matcher("a012").find());
        System.out.println("=======");

        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{},2021-10-14 14:56:48,260|ERROR||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|WARN||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|INF1O1||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport Exception:{}").find());
        System.out.println(ERROR_LINE_PATTERN.matcher("2021-10-14 14:56:48,260|WARN||ExecutorUtil-STP-Thread3|c.x.m.l.agent.channel.ChannelServiceImpl|doExport|345|doExport exception:{}").find());

    }

    @Test
    public void test3() {
        String str = "2021-12-17 14:00:00,030|INFO |f4262ed83de99780b2d058ede1fdc158|Dispatcher_Slow_Executor-thread-191|com.xiaomi.nrme.filter.ProretailFilter|tesla|request#HttpObjectAggregator$AggregatedFullHttpRequest(decodeResult: success, version: HTTP/1.1, content: CompositeByteBuf(ridx: 0, widx: 41, cap: 41, components=1))\n";
        String str1 = "POST /mtop/nrme/proretail/v1/home/info HTTP/1.1\n";
        String str2 = "Host: xmmionegw-outer.be.mi.com\n";
        String str3 = "X-Forwarded-For: 183.199.89.106\n";
        String str4 = "X-Real-IP: 183.199.89.106\n";
        String str5 = "Content-Length: 41\n";
        String str6 = "ua-pixels: 1152x2268\n";
        String str7 = "x-user-agent: channel/mishop platform/bimihome.android\n";
        String str8 = "smartmijia-client-versioncode: 10350\n";
        String str9 = "smartmijia-client-versionname: 1.0.35\n";
        String str10 = "Content-Type: application/json\n";
        String str11 = "Accept-Encoding: gzip\n";
        String str12 = "Cookie: cUserId=PyAYORHD1HxrvLonxSOotPJhDGg; serviceToken=N79GOsVpme1tllNVzDh7e6uKuGbhdNESQcUlACeC3YUgC4PeIEijBSZJyJGHxNDLVqmvl88gtzSo5QWdEdFYtb2qBU9I+mBuER5NZxYR4YQxltw/+woEsMgIC5bN2fEui1gf+O2YKm0QTd59sIn3hZ1Bz9IB8JMGCXiuUcDAZFY=; rn_version=20211216105308; masid=101.000; client_id=180100031055; Hm_lvt_4982d57ea12df95a2b24715fb6440726=1639664118; mstuid=1639664118290_331; pageid=8109f797e486bb5f; mstz=||483933613.2|||; xm_vistor=1639664118290_331_1639664118293-1639664151308; Hm_lpvt_4982d57ea12df95a2b24715fb6440726=1639664151\n";
        String str13 = "User-Agent: Dalvik/2.1.0 (Linux; U; Android 10; NOH-AN00 Build/HUAWEINOH-AN00) MIOTWeex/2.0.2 (BiMiHome;1.0.35;D525A231D5F55CAE130D84F767EB4F30;0.20.1;A;9803124E27F195C4F754E60F6EF0A6BF17DCB8F2;SmartMijia;WQqONV-D7UMw0dXK;) MIOTStore/20191212 (BiMiHome;1.0.35;D525A231D5F55CAE130D84F767EB4F30;20211216105308;A;9803124E27F195C4F754E60F6EF0A6BF17DCB8F2;SmartMijia;WQqONV-D7UMw0dXK;)\n";
        String str14 = "SmartMijia-Client-Id: 180100041197\n";
        String str15 = "SmartMijia-Client-VersionCode: 10350\n";
        String str16 = "SmartMijia-Client-VersionName: 1.0.35\n";
        String str17 = "SmartMijia-OSVersion: 10\n";
        String str18 = "X-trace-id: f4262ed83de99780b2d058ede1fdc158\n";
        String str19 = "traceparent: 00-f4262ed83de99780b2d058ede1fdc158-4fc9b0de924f8850-01 body#[{\"business_id\":\"\",\"store_id\":\"SQ69276\"}] configId#58003 api#online dubboApi#null cost#0";
        String str20 = "2021-12-17 14:00:00,031|INFO |f4262ed83de99780b2d058ede1fdc158|Dispatcher_Slow_Executor-thread-191|c.x.n.f.handle.LoginAccountPreHandle|tesla|url#/mtop/nrme/proretail/v1/home/info,group#online,serviceToken#N79GOsVpme1tllNVzDh7e6uKuGbhdNESQcUlACeC3YUgC4PeIEijBSZJyJGHxNDLVqmvl88gtzSo5QWdEdFYtb2qBU9I+mBuER5NZxYR4YQxltw/+woEsMgIC5bN2fEui1gf+O2YKm0QTd59sIn3hZ1Bz9IB8JMGCXiuUcDAZFY=,onlineTag#null\n";
        MLog log = new MLog();
        System.out.println(log.append(str));
        System.out.println(log.append(str1));
        System.out.println(log.append(str2));
        System.out.println(log.append(str3));
        System.out.println(log.append(str20));
    }
}
