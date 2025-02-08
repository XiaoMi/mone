package run.mone.m78.api;

import run.mone.m78.api.bo.im.ExecuteBotReqDTO;
import run.mone.m78.api.bo.im.HasBotReqDTO;
import run.mone.m78.api.bo.im.IMRecordDTO;
import run.mone.m78.api.bo.im.M78IMRelationDTO;

import java.math.BigInteger;
import java.util.List;

public interface IMRecordProvider {

    List<M78IMRelationDTO> hasBot(HasBotReqDTO reqDTO);

    IMRecordDTO get(IMRecordDTO imRecord);

    Boolean add(IMRecordDTO imRecord);

    Boolean delete(IMRecordDTO imRecord);

    String executeBot(ExecuteBotReqDTO reqDTO);

    String executeBot(String userName, Long botId, String input, String topicId);

}
