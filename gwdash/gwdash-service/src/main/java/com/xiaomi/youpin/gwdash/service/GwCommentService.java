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

import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.tesla.im.bo.CommentQueryVo;
import com.xiaomi.youpin.tesla.im.bo.CommentVo;
import com.xiaomi.youpin.tesla.im.service.CommentService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author tsingfu
 */
@Service
public class GwCommentService {

    @Reference(check = false, interfaceClass = CommentService.class, retries = 0,  group = "${dubbo.group}")
    private CommentService commentService;


    public Result<Boolean> comment(CommentVo commentVo) {
        commentService.comment(commentVo);
        return Result.success(true);
    }


    public Result<Boolean> reply(CommentVo commentVo) {
        commentService.reply(commentVo);
        return Result.success(true);
    }

    public Result<CommentVo> fetch(CommentVo commentVo) {
        return Result.success(commentService.fetch(commentVo).getData());
    }

    public Result<List<CommentVo>> query(CommentVo commentVo) {
        return Result.success(commentService.query(commentVo).getData());
    }

    public Result<Map<String, Object>> fuzzyQuery(CommentQueryVo commentQueryVo) {
        Map<String, Object> map = commentService.fuzzyQuery(commentQueryVo).getData();
        return Result.success(map);
    }

    public Result<Boolean> delete(CommentVo commentVo) {
        commentService.delete(commentVo);
        return Result.success(true);
    }

    public Result<Boolean> modify(CommentVo commentVo) {
        commentService.modify(commentVo);
        return Result.success(true);
    }
}
