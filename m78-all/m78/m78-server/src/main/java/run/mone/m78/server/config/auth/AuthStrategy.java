package run.mone.m78.server.config.auth;

import org.apache.commons.lang3.tuple.Pair;
import run.mone.m78.service.bo.user.SessionAccount;

import javax.servlet.http.HttpServletRequest;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 14:42
 */
public interface AuthStrategy {

    Pair<Integer, String> checkAuth(SessionAccount account, RoleControl roleControl, HttpServletRequest request);

}
