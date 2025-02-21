package run.mone.local.docean.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import run.mone.local.docean.bo.FileRequest;
import run.mone.local.docean.service.ZService;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class KnowledgeController {

    @Resource
    private ZService zService;

//    @RequestMapping(path = "/knowledge/list", method = "get")
//    public Result<List<ZKnowledgeBaseDTO>> getKnowledgeList(@RequestParam("username") String username) {
//        return Result.success(zService.getKnowledgeList(username));
//    }

//    @RequestMapping(path = "/knowledge/file/list", method = "get")
//    public Result<List<ZKnowledgeBaseFilesDTO>> getKnowledgeFileList(@RequestParam("username") String account,
//                                                                     @RequestParam("knowledgeId") Long knowledgeId) {
//        return Result.success(zService.listKnowledgeBaseFiles(knowledgeId, null, account));
//    }

//    @RequestMapping(path = "/knowledge/file/details", method = "get")
//    public Result<List<ZKnowledgeBaseFileBlockDTO>> getKnowledgeFileDetails(@RequestParam("account") String account,
//                                                                     @RequestParam("knowledgeId") Long knowledgeId,
//                                                                     @RequestParam("fileId") Long fileId) {
//        return Result.success(zService.listKnowledgeBaseFileBlocks(knowledgeId, fileId, account));
//    }

//    @RequestMapping(path = "/knowledge/update/file", method = "post")
//    public Result<ZKnowledgeBaseFileBlockDTO> updateKnowledgeFile(@RequestBody FileRequest request) {
//        return Result.success(zService.updateKnowledgeBaseFileBlock(request.getKnowledgeId(), request.getAccount(), request.getDto()));
//    }
//
//    @RequestMapping(path = "/knowledge/delete/file", method = "get")
//    public Result<Void> updateKnowledgeFile(@RequestParam("account") String account,
//                                            @RequestParam("knowledgeId") Long knowledgeId,
//                                            @RequestParam("fileId") Long fileId) {
//        List<Long> list = Collections.singletonList(fileId);
//        return Result.success(zService.deleteFileInKnowledgeBase(knowledgeId, list, account));
//    }
//
//    @RequestMapping(path = "/knowledge/delete/block", method = "get")
//    public Result<Boolean> deleteBlock(@RequestParam("knowledgeId") Long knowledgeId,
//                                       @RequestParam("account") String account,
//                                       @RequestParam("fileId") Long fileId,
//                                       @RequestParam("blockId") String blockId){
//        return Result.success(zService.deleteKnowledgeBaseFileBlock(knowledgeId, account, fileId, blockId));
//    }
}
