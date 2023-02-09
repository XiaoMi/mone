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

namespace java io.github.tesla.gateway.po.user

struct ValidatorConfig{
    //是否校验登录态已过期
    1:required bool CheckExpire;
    //是否校验用户修改密码
    2:required bool CheckPasswd;
    //是否校验userId和cUserId这两个cookie
    3:required bool CheckUserId;
}

struct ParseCookieRequest{
    1:required string Cookie;
    //线上服务可以不用传递
    2:optional ValidatorConfig Config;
}

struct ParseCookieResponse{
    1:required i64 UserId;
}

service PassportProxy{
    ParseCookieResponse ParseCookie(1:ParseCookieRequest req)
}