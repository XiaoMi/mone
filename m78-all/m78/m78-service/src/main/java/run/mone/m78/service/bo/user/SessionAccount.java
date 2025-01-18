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

package run.mone.m78.service.bo.user;

import lombok.Data;

@Data
public class SessionAccount {

    private Long userId;

    private String username;

    private Integer userType;

    private String name;

    private String email;

    private String tenant;

    private boolean admin;

    public SessionAccount() {
    }

    public SessionAccount(String username, Integer userType, String name, String tenant, boolean admin) {
        this.username = username;
        this.userType = userType;
        this.name = name;
        this.tenant = tenant;
        this.admin = admin;
    }

    public SessionAccount(Long userId, String username, Integer userType, String name, String tenant, boolean admin) {
        this.username = username;
        this.userType = userType;
        this.name = name;
        this.tenant = tenant;
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

}
