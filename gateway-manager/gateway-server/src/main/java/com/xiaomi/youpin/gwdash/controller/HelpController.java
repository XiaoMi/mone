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
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.bo.HelpVideoBo;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.HelpVideo;
//import com.xiaomi.youpin.gwdash.service.HelpService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
///**
// * @author tsingfu。
// */
//@RestController
//public class HelpController {
//
//    @Autowired
//    private HelpService helpService;
//
//    @RequestMapping("/api/help/video/list")
//    public Result<List<HelpVideo>> getListVideo() {
//        return helpService.listVideo();
//    }
//
//    @RequestMapping(value = "/api/help/video/add", method = RequestMethod.POST)
//    public Result<Boolean> getAddVideo(HttpServletRequest request, @RequestBody HelpVideoBo helpVideoBo) {
//        return helpService.addVideo(helpVideoBo);
//    }
//
//    @RequestMapping("/api/help/video/del")
//    public Result<Boolean> getDelVideo(@RequestParam("id") long id) {
//        return helpService.delVideo(id);
//    }
//}
