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
//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.bo.HelpVideoBo;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.HelpVideo;
//import lombok.extern.slf4j.Slf4j;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@Slf4j
//public class HelpService {
//
//    @Autowired
//    private Dao dao;
//
//    public Result<List<HelpVideo>> listVideo() {
//        return Result.success(dao.query(HelpVideo.class, null));
//    }
//
//    public Result<Boolean> delVideo(long id) {
//        HelpVideo helpVideo = dao.fetch(HelpVideo.class, Cnd.where("id", "=", id));
//        if (null != helpVideo) {
//            dao.delete(helpVideo);
//            return Result.success(true);
//        }
//        return new Result<>(1,"不存在", false);
//    }
//
//    public Result<Boolean> addVideo(HelpVideoBo helpVideoBo) {
//        long now = System.currentTimeMillis();
//        HelpVideo helpVideo = new HelpVideo();
//        helpVideo.setUrl(helpVideoBo.getUrl());
//        helpVideo.setTag(helpVideoBo.getTag());
//        helpVideo.setDescription(helpVideoBo.getDescription());
//        helpVideo.setCtime(now);
//        helpVideo.setUtime(now);
//        dao.insert(helpVideo);
//        return Result.success(true);
//    }
//}
