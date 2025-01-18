package run.mone.m78.service.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.mapper.M78BotFsmMapper;

import javax.annotation.Resource;

/**
 * @author wmin
 * @date 2024/5/24
 */
@Service
@Slf4j
public class BotFsmService {

    @Resource
    private M78BotFsmMapper botFsmMapper;

    private boolean saveBotFsm(String username){
        return true;
    }


}
