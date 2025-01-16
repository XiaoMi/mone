package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.chat.ChatTopicSearchReq;
import run.mone.m78.service.bo.chat.ChatAskParam;
import run.mone.m78.service.bo.chat.ChatTopicBo;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.ChatMessagePo;
import run.mone.m78.service.dao.entity.ChatTopicPo;
import run.mone.m78.service.dao.entity.ChatTopicTypeEnum;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.chat.ChatDBService;
import run.mone.m78.service.service.chat.ChatService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;


/**
 * @author goodjava@qq.com
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/chat")
@HttpApiModule(value = "ChatController", apiController = ChatController.class)
public class ChatController {

    @Autowired
    private LoginService loginService;

    @Resource
    private ChatDBService chatDBService;

    @Resource
    private ChatService chatService;


    //删除一条ChatMessage(project)
    @RequestMapping(value = "/chatmessage/delete", method = RequestMethod.POST)
    public Result<Void> deleteChatMessage(HttpServletRequest request,
                                          @RequestParam int messageId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.deleteChatMessageById(messageId, userName);
    }

    //插入一条ChatMessage(project)
    @RequestMapping(value = "/chatmessage/add", method = RequestMethod.POST)
    public Result<ChatMessagePo> insertChatMessage(HttpServletRequest request,
                                                   @RequestBody ChatMessagePo chatMessage) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        long now = System.currentTimeMillis();
        chatMessage.setState(1);
        chatMessage.setCtime(now);
        chatMessage.setUtime(now);
        chatMessage.setUserName(account.getUsername());
        return chatDBService.insertNewChatMessage(chatMessage);
    }

    //创建一个新的chatTopic(project)
    @RequestMapping(value = "/chattopic/add", method = RequestMethod.POST)
    public Result<ChatTopicPo> createChatTopic(HttpServletRequest request,
                                               @RequestBody ChatTopicPo chatTopic) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        chatTopic.setType(ChatTopicTypeEnum.AI_CHAT.getCode());
        return chatDBService.createChatTopic(chatTopic, userName);
    }

    //删除一个chatTopic(class)
    @RequestMapping(value = "/chattopic/delete", method = RequestMethod.POST)
    public Result<Void> deleteChatTopic(HttpServletRequest request,
                                        @RequestParam int topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.deleteChatTopicById(topicId, userName);
    }

    //查询某个chatTopic下的所有chatMessage(class)
    @RequestMapping(value = "/chatmessage/list", method = RequestMethod.GET)
    public Result<List<ChatMessagePo>> listChatMessagesByTopicId(HttpServletRequest request,
                                                                 @RequestParam int topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.getAllChatMessagesByTopicId(topicId, userName);
    }

    //查询某个userName下的所有messageTopic(project)
    @RequestMapping(value = "/messagetopic/list", method = RequestMethod.GET)
    public Result<List<ChatTopicPo>> listMessageTopicsByUserName(HttpServletRequest request,
                                                                 @RequestParam(value = "type", defaultValue = "0", required = false) int type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.getAllChatTopicsByUserName(userName, type);
    }

    @RequestMapping(value = "/messagetopic/detail", method = RequestMethod.GET)
    public Result<ChatTopicBo> messageTopicDetail(HttpServletRequest request,
                                                  @RequestParam int topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.chatTopicDetail(topicId, userName);
    }

    //按topic id清空chatMessage,需要验证userName(project)
    @RequestMapping(value = "/message/clear", method = RequestMethod.GET)
    public Result<Void> clearChatMessages(HttpServletRequest request,
                                          @RequestParam int topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        return chatDBService.clearChatMessagesByTopicId(topicId, userName);
    }

    //修改chatTopic信息(project)
    @RequestMapping(value = "/chattopic/update", method = RequestMethod.POST)
    public Result<ChatTopicPo> updateChatTopic(HttpServletRequest request,
                                               @RequestBody ChatTopicPo chatTopic) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        chatTopic.setUserName(userName);
        return chatDBService.updateChatTopic(chatTopic);
    }

    @RequestMapping(value = "/chattopic/findOrCreate", method = RequestMethod.POST)
    public Result<List<ChatTopicPo>> findOrCreateChatTopics(HttpServletRequest request,
                                                            @RequestBody ChatTopicSearchReq searchReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        searchReq.setUsername(userName);
        return chatDBService.findOrCreateChatTopics(searchReq);
    }

    @RequestMapping(value = {"/ask"}, method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> ask(HttpServletRequest request,
                                          @RequestBody ChatAskParam chatAskParam) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());

        SessionAccount account = loginService.getAccountFromSession(request);
        chatAskParam.setM78UserName(account.getUsername());
        SseEmitter emitter = chatService.chatStream(chatAskParam);

        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

}
