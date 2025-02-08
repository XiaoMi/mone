package run.mone.m78.server.ws.biz;

import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.server.ws.WsSessionHolder;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.bo.BaseResult;
import run.mone.m78.service.bo.chat.ChatMessage;
import run.mone.m78.service.bo.chat.FriendsMessage;
import run.mone.m78.service.bo.chat.PingMessage;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.IMFriendshipStatusEnum;
import run.mone.m78.service.dto.friend.*;
import run.mone.m78.service.service.friend.IMFriendService;

import java.util.List;
import java.util.Optional;

/**
 * @author wmin
 * @date 2024/5/11
 *
 * 处理好友信息的
 */
@Slf4j
@Service
public class IMFriendHandlerService {

    @Autowired
    private IMFriendService imFriendService;

    /**
     * 处理IM好友相关的操作，根据传入的JSON对象和用户信息执行相应的命令。
     *
     * @param jsonObject 包含命令和相关数据的JSON对象
     * @param appUser    应用用户标识，格式为 "appId_userName"
     */

	public void handleIMFriend(JsonObject jsonObject, String appUser) {
        if (jsonObject.has("cmd")) {
            log.info("cmd:{}", jsonObject);
            String cmd = jsonObject.get("cmd").getAsString();
            //测试连通性
            if (cmd.equals("ping")) {
                WsSessionHolder.INSTANCE.sendMessage(appUser, new PingMessage());
                return;
            }

            Integer appId = Integer.parseInt(appUser.split("_")[0]);
            String userName = appUser.split("_")[1];
            //给好友发信息
            if (cmd.equals("chat")) {
                ChatMessage chatMessage = GsonUtils.gson.fromJson(jsonObject, ChatMessage.class);
                Optional.ofNullable(chatMessage.getMetaInfo()).orElseGet(() -> {
                    BaseMessage.MetaInfo metaInfo = new BaseMessage.MetaInfo();
                    chatMessage.setMetaInfo(metaInfo);
                    return metaInfo;
                }).setAppId(appId);

                if (sendErrorMessageIfNotFriends(appUser, chatMessage, appId)) {
                    return;
                }
                if (WsSessionHolder.INSTANCE.USER_SESSIONID.containsKey(appId + "_" + chatMessage.getTo())) {
                    imFriendService.saveChatMessage(chatMessage, 1);
                    WsSessionHolder.INSTANCE.sendMessage(appId + "_" + chatMessage.getTo(), chatMessage);
                } else {
                    imFriendService.saveChatMessage(chatMessage, 2);
                }
            }
            //查找用户
            if (cmd.equals("searchUser")) {
                String searchUserName = jsonObject.get("userName").getAsString();
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                List<IMUserDto> userDtos = imFriendService.searchUsersByUserName(searchUserName, appId);
                FriendsMessage userListMessage = FriendsMessage.builder().users(userDtos).build();
                userListMessage.setMetaInfo(metaInfo);
                WsSessionHolder.INSTANCE.sendMessage(appUser, userListMessage);
            }
            //获取好友列表
            if (cmd.equals("friends")) {
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                List<IMUserDto> userDtos = imFriendService.findFriendsByUsername(ReqIMFriendDto.builder().userName(userName).appId(appId).build());
                FriendsMessage friendsMessage = FriendsMessage.builder().users(userDtos).build();
                friendsMessage.setMetaInfo(metaInfo);
                WsSessionHolder.INSTANCE.sendMessage(appUser, friendsMessage);
            }
            //发送好友申请
            if (cmd.equals("addFriend")) {
                String friendUserName = jsonObject.get("friendUserName").getAsString();
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                Result<Boolean> rst = imFriendService.addFriendByUsername(userName, friendUserName, appId);
                BaseMessage rstMessage = new BaseMessage();
                rstMessage.setCode(rst.getCode() == 0 && rst.getData() ? 0 : -1);
                rstMessage.setMsg(rst.getMessage());
                rstMessage.setMetaInfo(metaInfo);
                WsSessionHolder.INSTANCE.sendMessage(appUser, rstMessage);
                if (rstMessage.getCode() == 0) {
                    notifyFriendRequestPending(appId, userName, friendUserName, metaInfo);
                }
            }
            //我的待处理好友申请
            if (cmd.equals("pendingFriendList")) {
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                List<FriendReqDto> friendReqDtos = imFriendService.listPendingFriendRequests(userName, appId);
                BaseResult<List<FriendReqDto>> result = new BaseResult(0, metaInfo, friendReqDtos);
                WsSessionHolder.INSTANCE.sendMessage(appUser, result);
            }
            //操作好友关系
            if (cmd.equals("handleFriendship")) {
                ReqIMFriendshipDto reqIMFriendshipDto = GsonUtils.gson.fromJson(jsonObject, ReqIMFriendshipDto.class);
                reqIMFriendshipDto.setAppId(appId);
                reqIMFriendshipDto.setCurrentUserName(userName);
                Result<Boolean> rst = imFriendService.handleFriendship(reqIMFriendshipDto);
                BaseResult rstMessage = new BaseResult(rst.getCode() == 0 && rst.getData() ? 0 : -1, rst.getMessage(), reqIMFriendshipDto.getMetaInfo());
                WsSessionHolder.INSTANCE.sendMessage(appUser, rstMessage);
                if (rstMessage.getCode() == 0) {
                    notifyFriendshipStatus(reqIMFriendshipDto);
                }
            }
            //跟某位好友的消息列表
            if (cmd.equals("listMessagesFromFriend")) {
                String friendUserName = jsonObject.get("friendUserName").getAsString();
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                List<MessageDetailDto> messageDetailDtos = imFriendService.listMessagesFromFriend(
                        userName,
                        friendUserName,
                        appId,
                        jsonObject.has("status") ? jsonObject.get("status").getAsInt() : null);
                BaseResult<List<MessageDetailDto>> result = new BaseResult(0, metaInfo, messageDetailDtos);
                WsSessionHolder.INSTANCE.sendMessage(appUser, result);
            }
            //未读消息列表
            if (cmd.equals("listUnreadMessages")) {
                BaseMessage.MetaInfo metaInfo = GsonUtils.gson.fromJson(jsonObject.get("metaInfo"), BaseMessage.MetaInfo.class);
                List<MessageUnreadDto> unreadMessages = imFriendService.listUnreadMessages(userName, appId);
                BaseResult<List<MessageUnreadDto>> result = new BaseResult(0, metaInfo, unreadMessages);
                WsSessionHolder.INSTANCE.sendMessage(appUser, result);
            }
            //将消息置为已读
            if (cmd.equals("markMessagesAsRead")) {
                MessageReadDto messageReadDto = GsonUtils.gson.fromJson(jsonObject, MessageReadDto.class);
                messageReadDto.setAppId(appId);
                messageReadDto.setCurrentUserName(userName);
                Result<Boolean> rst = imFriendService.markMessagesAsRead(messageReadDto);
                BaseResult rstMessage = new BaseResult(rst.getCode() == 0 && rst.getData() ? 0 : -1, rst.getMessage(), messageReadDto.getMetaInfo());
                WsSessionHolder.INSTANCE.sendMessage(appUser, rstMessage);
            }
        }
    }

    private boolean sendErrorMessageIfNotFriends(String appUser, ChatMessage chatMessage, Integer appId) {
        if (!imFriendService.isFriends(chatMessage.getFrom(), chatMessage.getTo(), appId)) {
            BaseMessage rstMessage = new BaseMessage();
            rstMessage.setCode(-1);
            rstMessage.setMsg(chatMessage.getTo() + " is not your friend");
            rstMessage.setMetaInfo(chatMessage.getMetaInfo());
            WsSessionHolder.INSTANCE.sendMessage(appUser, rstMessage);
            return true;
        }
        return false;
    }

    /**
     * 更新用户在线状态
     *
     * @param user 用户标识符，格式为 "appId_userName"
     * @param isOnline 用户是否在线的状态
     * @return 更新操作的结果，包含一个布尔值，表示操作是否成功
     */
	public Result<Boolean> updateUserOnlineStatus(String user, boolean isOnline) {
        Integer appId = Integer.parseInt(user.split("_")[0]);
        String userName = user.split("_")[1];
        return imFriendService.updateUserOnlineStatus(appId, userName, isOnline);
    }


    /**
     * 通知用户有好友申请待处理
     */
    private void notifyFriendRequestPending(Integer appId, String currentUserName, String friendUserName, BaseMessage.MetaInfo metaInfo) {
        if (!WsSessionHolder.USER_SESSIONID.containsKey(appId + "_" + friendUserName)) {
            log.info("notifyFriendRequestPending {} is not online", friendUserName);
        }
        log.info("notifyFriendRequestPending start {}", friendUserName);
        ReqIMFriendshipDto friendshipDto = ReqIMFriendshipDto.builder().friendUserName(currentUserName).action(IMFriendshipStatusEnum.PENDING.getCode()).build();
        friendshipDto.setMetaInfo(metaInfo);
        WsSessionHolder.INSTANCE.sendMessage(appId + "_" + friendUserName, friendshipDto);
    }

    /**
     * 通知用户好友申请的处理结果
     */
    private void notifyFriendshipStatus(ReqIMFriendshipDto reqIMFriendshipDto) {
        if (!WsSessionHolder.USER_SESSIONID.containsKey(reqIMFriendshipDto.getAppId() + "_" + reqIMFriendshipDto.getFriendUserName())) {
            log.info("notifyFriendshipStatus {} is not online", reqIMFriendshipDto.getFriendUserName());
        }
        log.info("notifyFriendshipStatus start {} ", reqIMFriendshipDto.getFriendUserName());
        ReqIMFriendshipDto friendshipDto = ReqIMFriendshipDto.builder()
                .friendUserName(reqIMFriendshipDto.getCurrentUserName())
                .action(reqIMFriendshipDto.getAction())
                .build();
        friendshipDto.setMetaInfo(reqIMFriendshipDto.getMetaInfo());
        WsSessionHolder.INSTANCE.sendMessage(reqIMFriendshipDto.getAppId() + "_" + reqIMFriendshipDto.getFriendUserName(), friendshipDto);
    }


}
