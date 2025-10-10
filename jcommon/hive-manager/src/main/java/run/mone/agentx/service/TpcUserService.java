package run.mone.agentx.service;

import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author wmin
 * @date 2025/9/30
 */
@Service
@RequiredArgsConstructor
public class TpcUserService {

    /**
     * 获取用户账号信息
     * @return
     */
    public Mono<AuthUserVo> getUserInfo(){
        AuthUserVo userVo = UserUtil.getUser();
        // 适配private/api类型的请求
        if(userVo == null){
            return Mono.empty();
        }

        return Mono.just(userVo);
    }

}
