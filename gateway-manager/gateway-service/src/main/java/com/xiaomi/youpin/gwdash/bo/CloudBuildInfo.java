///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.bo;
//
//import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
//import lombok.Data;
//import org.nutz.dao.entity.annotation.*;
//
//
//@Data
//@Table(value = "docker_image_info")
//public class CloudBuildInfo {
//    @Id
//    private int id;
//
//    @Column("compilation_id")
//    private long compilationId;
//
//    @Column("project_id")
//    private long projectId;
//
//    @Column(wrap = true)
//    private String desc;
//
//    @Column("git_address")
//    private String gitAddress;
//
//    @Column("group_name")
//    private String groupName;
//
//    @Column("project_name")
//    private String projectName;
//
//    @Column("commit_id")
//    private String commitId;
//
//    @Column
//    private int status;
//
//    @Column
//    private String creator;
//
//    @Column
//    private String updater;
//
//    @Column
//    private int type;
//
//    @Column
//    private long ctime;
//
//    @Column
//    private long utime;
//
//    @Column
//    private String branch;
//
//
//
//    @One(field = "compilationId", target = ProjectCompileRecord.class)
//    private ProjectCompileRecord buildRecord;
//}
