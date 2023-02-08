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

package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.entity.Group;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class SessionAccount {

    private Long id;

    private Long uuid;

    private String username;

    private String name;

    private String token;

    private Integer role;

    private String email;

    private String gid;
    private List<ResourceBo> resource;




    private List<Group> gidInfos;
    public List<ResourceBo> getResource() {
        return resource;
    }

    public void setResource(List<ResourceBo> resource) {
        this.resource = resource;
    }

    public List<Group> getGidInfos() {
        return gidInfos;
    }

    public void setGidInfos(List<Group> gidInfos) {
        this.gidInfos = gidInfos;
    }

    private List<RoleBo> roles = new ArrayList<>();

    public SessionAccount() {}

    public SessionAccount(Long id, String username, String name, String token, Integer role, List<RoleBo> roles) {
        this.id = id;
        this.uuid = id;
        this.username = username;
        this.name = name;
        this.token = token;
        this.role = role;
        this.roles = roles;
    }

    public SessionAccount(Long id, String username, String name, String token, Integer role, String gid, List<RoleBo> roles, List<Group> gidInfos) {
        this.id = id;
        this.uuid = id;
        this.username = username;
        this.name = name;
        this.token = token;
        this.role = role;
        this.gid = gid;
        this.roles = roles;
        this.gidInfos=gidInfos;
    }
    public SessionAccount(Long id, String username, String name, String token, Integer role, String gid, List<RoleBo> roles, List<Group> gidInfos,List<ResourceBo> resources) {
        this.id = id;
        this.uuid = id;
        this.username = username;
        this.name = name;
        this.token = token;
        this.role = role;
        this.gid = gid;
        this.roles = roles;
        this.gidInfos=gidInfos;
        this.resource = resources;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setRoles(List<RoleBo> roles) { this.roles = roles; }

    public List<RoleBo> getRoles() { return this.roles; }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    @Override
    public String toString() {
        return "SessionAccount{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", role=" + role +
                ", gid=" + gid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionAccount that = (SessionAccount) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(username, that.username) &&
                Objects.equals(name, that.name) &&
                Objects.equals(token, that.token) &&
                Objects.equals(role, that.role) &&
                Objects.equals(gid, that.gid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, username, name, token, role, gid);
    }
}
