package run.mone.m78.service.bo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.dto.friend.IMUserDto;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 11:19
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FriendsMessage extends BaseMessage {

    private List<IMUserDto> users;

}
