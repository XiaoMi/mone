package com.xiaomi.mone.tpc.system;

import com.xiaomi.mone.tpc.common.vo.SystemVo;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/7/25 14:09
 */
public interface SystemHelper {

    SystemVo getVoByToken(String system, String token);

    String createSysToken(String systemName, String userAccount);

}
