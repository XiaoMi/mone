package run.mone.m78.service.service.friend;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import run.mone.m78.service.bo.chat.ChatMessage;
import run.mone.m78.service.dao.entity.IMFriendshipPo;
import run.mone.m78.service.dao.entity.IMFriendshipStatusEnum;
import run.mone.m78.service.dao.entity.IMMessagePo;
import run.mone.m78.service.dao.entity.IMUserPo;
import run.mone.m78.service.dao.mapper.M78IMFriendshipMapper;
import run.mone.m78.service.dao.mapper.M78IMMessageMapper;
import run.mone.m78.service.dao.mapper.M78IMUserMapper;
import run.mone.m78.service.dto.friend.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.service.dao.entity.table.IMFriendshipPoTableDef.I_M_FRIENDSHIP_PO;
import static run.mone.m78.service.dao.entity.table.IMMessagePoTableDef.I_M_MESSAGE_PO;
import static run.mone.m78.service.dao.entity.table.IMUserPoTableDef.I_M_USER_PO;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author wmin
 * @date 2024/5/9
 */
@Service
@Slf4j
public class IMFriendService {
    @Resource
    private M78IMFriendshipMapper friendshipMapper;

    @Resource
    private M78IMUserMapper userMapper;

    @Resource
    private M78IMMessageMapper messageMapper;

    /**
     * 更新用户的在线状态。如果用户不存在则创建用户，存在则更新在线状态。
     * 如果用户上线，则更新最后一次在线时间。
     *
     * @param appId 应用ID
     * @param username 用户名
     * @param isOnline 是否在线
     * @return 操作结果，成功返回true
     */
	// 用户上下线，如果用户不存在则创建，存在则更新在线状态，上线的话需要更新last seen字段(project)
    public Result<Boolean> updateUserOnlineStatus(Integer appId, String username, boolean isOnline) {
        IMUserPo user = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", username));
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (user == null) {
            user = IMUserPo.builder()
                    .appId(appId)
                    .userName(username)
                    .isOnline(isOnline)
                    .lastSeen(currentTime)
                    .createTime(currentTime)
                    .build();
            userMapper.insert(user);
        } else {
            user.setOnline(isOnline);
            if (isOnline) {
                user.setLastSeen(currentTime);
            }
            userMapper.update(user);
        }
        return Result.success(true);
    }

    /**
     * 判断两用户是否是好友关系
     *
     * @param username1 用户名1
     * @param username2 用户名2
     * @param appId 应用ID
     * @return 如果两用户是好友关系返回true，否则返回false
     */
	//判断两用户是否是好友关系(project)
    public boolean isFriends(String username1, String username2, Integer appId) {
        IMUserPo user1 = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", username1));
        IMUserPo user2 = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", username2));
        if (user1 == null || user2 == null) {
            return false;
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(I_M_FRIENDSHIP_PO.USER_ID1.eq(user1.getId()).and(I_M_FRIENDSHIP_PO.USER_ID2.eq(user2.getId())).and(I_M_FRIENDSHIP_PO.STATUS.eq(IMFriendshipStatusEnum.ACCEPTED.getCode())))
                .or(I_M_FRIENDSHIP_PO.USER_ID2.eq(user1.getId()).and(I_M_FRIENDSHIP_PO.USER_ID1.eq(user2.getId())).and(I_M_FRIENDSHIP_PO.STATUS.eq(IMFriendshipStatusEnum.ACCEPTED.getCode())));
        IMFriendshipPo friendship = friendshipMapper.selectOneByQuery(queryWrapper);
        return friendship != null;
    }


    /**
     * 根据用户名添加好友
     *
     * @param currentUserName 当前用户的用户名
     * @param friendUserName  朋友的用户名
     * @param appId           应用ID
     * @return 返回一个包含布尔值的Result对象，表示操作是否成功
     */
	// 好友申请，根据userName添加好友 (project)
    public Result<Boolean> addFriendByUsername(String currentUserName, String friendUserName, Integer appId) {
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", currentUserName));
        if (currentUser == null) {
            return Result.fail(STATUS_NOT_FOUND, "Current user not found");
        }
        IMUserPo friendUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", friendUserName));
        if (friendUser == null) {
            return Result.fail(STATUS_NOT_FOUND, "Friend user not found");
        }
        //好友关系已存在，或者已申请是pending状态
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(I_M_FRIENDSHIP_PO.APP_ID.eq(appId))
                .and(I_M_FRIENDSHIP_PO.USER_ID1.eq(currentUser.getId()).and(I_M_FRIENDSHIP_PO.USER_ID2.eq(friendUser.getId())))
                .or(I_M_FRIENDSHIP_PO.USER_ID2.eq(currentUser.getId()).and(I_M_FRIENDSHIP_PO.USER_ID1.eq(friendUser.getId())));
        IMFriendshipPo imFriendshipPo = friendshipMapper.selectOneByQuery(queryWrapper);
        if (null == imFriendshipPo) {
            IMFriendshipPo friendship = IMFriendshipPo.builder()
                    .appId(appId)
                    .userId1(currentUser.getId())
                    .userId2(friendUser.getId())
                    .status(IMFriendshipStatusEnum.PENDING.getCode())
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .build();
            friendshipMapper.insert(friendship);
            log.info("insert friendship {}", friendship.getId());
            return Result.success(true);
        }
        if (IMFriendshipStatusEnum.PENDING.getCode() == imFriendshipPo.getStatus() || IMFriendshipStatusEnum.ACCEPTED.getCode() == imFriendshipPo.getStatus()) {
            return Result.fail(STATUS_BAD_REQUEST, "Friendship already exists or is pending");
        } else {
            imFriendshipPo.setStatus(IMFriendshipStatusEnum.PENDING.getCode());
            imFriendshipPo.setCreateTime(new Timestamp(System.currentTimeMillis()));
            friendshipMapper.update(imFriendshipPo);
            log.info("update friendship {}", imFriendshipPo.getId());
            return Result.success(true);
        }
    }


    /**
     * 获取当前用户的待处理好友申请列表
     *
     * @param currentUserName 当前用户名
     * @param appId 应用ID
     * @return 待处理好友申请的列表，如果当前用户不存在或没有待处理的好友申请则返回空列表
     */
	// 我的待处理好友申请列表(project)
    public List<FriendReqDto> listPendingFriendRequests(String currentUserName, Integer appId) {
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", currentUserName));
        if (currentUser == null) {
            return Collections.emptyList();
        }
        List<IMFriendshipPo> pendingFriendships = friendshipMapper.selectListByQuery(
                QueryWrapper.create().where(I_M_FRIENDSHIP_PO.STATUS.eq(IMFriendshipStatusEnum.PENDING.getCode())).and(I_M_FRIENDSHIP_PO.USER_ID2.eq(currentUser.getId())));
        if (pendingFriendships.isEmpty()) {
            return Collections.emptyList();
        }
        return pendingFriendships.stream().map(friendship -> {
            IMUserPo friendUser = userMapper.selectOneByQuery(new QueryWrapper().eq("id", friendship.getUserId1()));
            return FriendReqDto.builder()
                    .appId(friendUser.getAppId())
                    .userId(friendship.getUserId1())
                    .userName(friendUser.getUserName())
                    .reqTime(friendship.getCreateTime()).build();
        }).collect(Collectors.toList());
    }


    /**
     * 操作好友关系表，比如通过好友申请、拉黑好友等
     *
     * @param reqDto 包含好友关系操作信息的请求数据传输对象
     * @return 操作结果，成功返回true，失败返回相应的错误信息
     */
	// 操作好友关系表，比如通过好友申请、拉黑好友等，入参为ReqIMFriendshipDto (project)
    public Result<Boolean> handleFriendship(ReqIMFriendshipDto reqDto) {
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", reqDto.getAppId()).eq("username", reqDto.getCurrentUserName()));
        if (currentUser == null) {
            return Result.fail(STATUS_NOT_FOUND, "Current user not found");
        }
        IMUserPo targetUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", reqDto.getAppId()).eq("username", reqDto.getFriendUserName()));
        if (targetUser == null) {
            return Result.fail(STATUS_NOT_FOUND, "Target user not found");
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(I_M_FRIENDSHIP_PO.APP_ID.eq(reqDto.getAppId()))
                .and((I_M_FRIENDSHIP_PO.USER_ID1.eq(currentUser.getId()).and(I_M_FRIENDSHIP_PO.USER_ID2.eq(targetUser.getId())))
                        .or(I_M_FRIENDSHIP_PO.USER_ID2.eq(currentUser.getId()).and(I_M_FRIENDSHIP_PO.USER_ID1.eq(targetUser.getId()))));
        IMFriendshipPo friendship = friendshipMapper.selectOneByQuery(queryWrapper);
        if (friendship == null) {
            return Result.fail(STATUS_BAD_REQUEST, "Friendship does not exist");
        }
        switch (reqDto.getAction()) {
            case 1:
                if (friendship.getStatus() != IMFriendshipStatusEnum.PENDING.getCode()) {
                    return Result.fail(STATUS_BAD_REQUEST, "Friendship is not in pending status");
                }
                friendship.setStatus(IMFriendshipStatusEnum.ACCEPTED.getCode());
                break;
            case 2:
                if (friendship.getStatus() != IMFriendshipStatusEnum.PENDING.getCode()) {
                    return Result.fail(STATUS_BAD_REQUEST, "Friendship is not in pending status");
                }
                friendship.setStatus(IMFriendshipStatusEnum.REJECTED.getCode());
                break;
            case 3:
                friendship.setStatus(IMFriendshipStatusEnum.BLOCKED.getCode());
                break;
            case 4:
                friendship.setStatus(IMFriendshipStatusEnum.DELETED.getCode());
                break;
            default:
                return Result.fail(STATUS_BAD_REQUEST, "Invalid action");
        }
        friendshipMapper.update(friendship);
        return Result.success(true);
    }

    /**
     * 根据userName模糊匹配检索用户列表信息
     *
     * @param userName 用户名，支持模糊匹配
     * @param appId 应用ID
     * @return 匹配的用户列表，如果userName为空或没有匹配的用户则返回空列表
     */
	// 根据userName模糊匹配检索用户列表信息(project)
    public List<IMUserDto> searchUsersByUserName(String userName, Integer appId) {
        if (StringUtils.isBlank(userName)) {
            return Collections.emptyList();
        }
        List<IMUserPo> userPos = userMapper.selectListByQuery(
                QueryWrapper.create().eq("app_id", appId).like("username", userName)
        );
        if (CollectionUtils.isEmpty(userPos)) {
            return Collections.emptyList();
        }
        return userPos.stream().map(this::poToDto).collect(Collectors.toList());
    }

    /**
     * 根据用户名查询用户的好友列表
     *
     * @param imFriendDto 包含查询条件的请求对象，包含用户名、应用ID和在线状态等信息
     * @return 用户的好友列表，如果用户不存在或没有好友则返回空列表
     */
	// 根据userName查询用户的好友列表
    public List<IMUserDto> findFriendsByUsername(ReqIMFriendDto imFriendDto) {
        String userName = imFriendDto.getUserName();
        IMUserPo user = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", imFriendDto.getAppId()).eq("username", userName));
        if (user == null) {
            return Collections.emptyList();
        }

        List<IMFriendshipPo> friendships = friendshipMapper.selectListByQuery(
                QueryWrapper.create().where(I_M_FRIENDSHIP_PO.APP_ID.eq(imFriendDto.getAppId())).and(I_M_FRIENDSHIP_PO.STATUS.eq(IMFriendshipStatusEnum.ACCEPTED.getCode()))
                        .and(I_M_FRIENDSHIP_PO.USER_ID1.eq(user.getId()).or(I_M_FRIENDSHIP_PO.USER_ID2.eq(user.getId())))
        );
        if (friendships.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> friendIds = friendships.stream()
                .flatMap(f -> Stream.of(f.getUserId1(), f.getUserId2()))
                .filter(id -> !id.equals(user.getId()))
                .collect(Collectors.toSet());
        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<IMUserPo> userPos = userMapper.selectListByQuery(QueryWrapper.create().where(I_M_USER_PO.ID.in(friendIds))
                .and(null == imFriendDto.getIsOnline() ? noCondition() : I_M_USER_PO.IS_ONLINE.eq(imFriendDto.getIsOnline())));

        if (CollectionUtils.isEmpty(userPos)) {
            return Collections.emptyList();
        }
        return userPos.stream().map(i -> poToDto(i)).collect(Collectors.toList());
    }

    /**
     * 存储聊天消息，需要先根据用户名查询用户id
     *
     * @param chatMessage 聊天消息对象，包含发送者、接收者及消息内容等信息
     * @param status 消息状态
     * @return 包含操作结果的Result对象，成功时返回true，失败时返回错误信息
     */
	// 存储聊天消息，需要先根据用户名查询用户id(project)
    public Result<Boolean> saveChatMessage(ChatMessage chatMessage, int status) {
        IMUserPo sender = userMapper.selectOneByQuery(new QueryWrapper().eq("username", chatMessage.getFrom()).eq("app_id", chatMessage.getMetaInfo().getAppId()));
        if (sender == null) {
            return Result.fail(STATUS_NOT_FOUND, "Sender not found");
        }
        IMUserPo receiver = userMapper.selectOneByQuery(new QueryWrapper().eq("username", chatMessage.getTo()).eq("app_id", chatMessage.getMetaInfo().getAppId()));
        if (receiver == null) {
            return Result.fail(STATUS_NOT_FOUND, "Receiver not found");
        }
        IMMessagePo messagePo = IMMessagePo.builder()
                .appId(chatMessage.getMetaInfo().getAppId())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .message(chatMessage.getMessage())
                .sentTime(new Timestamp(System.currentTimeMillis()))
                .status(status)
                .build();
        messageMapper.insert(messagePo);
        return Result.success(true);
    }

    /**
     * 获取当前用户的某个好友的所有消息
     *
     * @param currentUserName 当前用户名
     * @param friendUserName 好友用户名
     * @param appId 应用ID
     * @param status 消息状态（可选）
     * @return 当前用户与好友之间的消息列表
     */
	// 获取当前用户的某个好友的所有消息(project)
    public List<MessageDetailDto> listMessagesFromFriend(String currentUserName, String friendUserName, Integer appId, Integer status) {
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", currentUserName));
        if (currentUser == null) {
            return Collections.emptyList();
        }
        IMUserPo friendUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", friendUserName));
        if (friendUser == null) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper =  QueryWrapper.create().where(I_M_MESSAGE_PO.RECEIVER_ID.eq(currentUser.getId()).and(I_M_MESSAGE_PO.SENDER_ID.eq(friendUser.getId())))
                .or(I_M_MESSAGE_PO.SENDER_ID.eq(currentUser.getId()).and(I_M_MESSAGE_PO.RECEIVER_ID.eq(friendUser.getId())))
                .orderBy("sent_time", true);
        if (null != status) {
            queryWrapper.eq("status", status);
        }
        List<IMMessagePo> unreadMessagesFromFriend = messageMapper.selectListByQuery(queryWrapper);
        if (unreadMessagesFromFriend.isEmpty()) {
            return Collections.emptyList();
        }
        return unreadMessagesFromFriend.stream().map(messagePo ->
                MessageDetailDto.builder()
                        .id(messagePo.getId())
                        .status(messagePo.getStatus())
                        .sender(messagePo.getSenderId().equals(currentUser.getId())?currentUserName:friendUserName)
                        .message(messagePo.getMessage())
                        .sendTime(messagePo.getSentTime())
                        .build()
        ).collect(Collectors.toList());
    }


    /**
     * 获取当前用户的未读消息列表，包含好友名和未读条数
     *
     * @param currentUserName 当前用户名
     * @param appId 应用ID
     * @return 未读消息列表，包含好友名和未读条数的MessageUnreadDto对象列表
     */
	//获取当前用户的未读列表，需要包含好友名和未读条数，出参为MessageUnreadDto(project)
    public List<MessageUnreadDto> listUnreadMessages(String currentUserName, Integer appId) {
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", currentUserName));
        if (currentUser == null) {
            return Collections.emptyList();
        }
        List<IMMessagePo> unreadMessages = messageMapper.selectListByQuery(QueryWrapper.create().eq("receiver_id", currentUser.getId()).eq("status", 2));
        if (unreadMessages.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Long> unreadCountBySenderId = unreadMessages.stream().collect(Collectors.groupingBy(IMMessagePo::getSenderId, Collectors.counting()));
        return unreadCountBySenderId.entrySet().stream().map(entry -> {
            IMUserPo friendUser = userMapper.selectOneByQuery(new QueryWrapper().eq("id", entry.getKey()));
            if (friendUser != null) {
                return MessageUnreadDto.builder().friendUsername(friendUser.getUserName()).unreadCount(entry.getValue().intValue()).build();
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 将消息置为已读
     *
     * @param messageReadDto 包含消息读取信息的DTO对象
     * @return 操作结果，成功时返回true
     */
	//将消息置为已读
    public Result<Boolean> markMessagesAsRead(MessageReadDto messageReadDto) {
        Integer appId = messageReadDto.getAppId();
        IMUserPo currentUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", messageReadDto.getCurrentUserName()));
        if (currentUser == null) {
            log.warn("User {} not found with appId {}", messageReadDto.getCurrentUserName(), appId);
            return Result.fail(STATUS_NOT_FOUND, "currentUser not found");
        }
        IMUserPo friendUser = userMapper.selectOneByQuery(new QueryWrapper().eq("app_id", appId).eq("username", messageReadDto.getFriendUserName()));
        if (friendUser == null) {
            log.warn("Friend user {} not found with appId {}", messageReadDto.getFriendUserName(), appId);
            return Result.fail(STATUS_NOT_FOUND, "friendUser not found");
        }
        QueryWrapper queryWrapper = QueryWrapper.create().eq("receiver_id", currentUser.getId()).eq("sender_id", friendUser.getId()).eq("status", 2);
        if (!CollectionUtils.isEmpty(messageReadDto.getMsgIds())) {
            queryWrapper.in("id", messageReadDto.getMsgIds());
        }
        List<IMMessagePo> messagesToUpdate = messageMapper.selectListByQuery(queryWrapper);
        messagesToUpdate.forEach(message -> {
            message.setStatus(1);
            messageMapper.update(message);
        });
        log.info("Updated messages to read status for user {} from user {}", messageReadDto.getCurrentUserName(), messageReadDto.getFriendUserName());
        return Result.success(true);
    }

    private IMUserDto poToDto(IMUserPo userPo) {
        IMUserDto dto = IMUserDto.builder()
                .id(userPo.getId())
                .appId(userPo.getAppId())
                .userName(userPo.getUserName())
                .isOnline(userPo.isOnline())
                .lastSeen(userPo.getLastSeen())
                .createTime(userPo.getCreateTime())
                .build();
        return dto;
    }


}
