package run.mone.m78.service.service.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

/**
 * GoogleIdTokenService类提供了验证Google ID令牌并提取用户信息的功能。
 * 该类使用Google的ID令牌验证器来验证传入的JWT令牌，并从中提取用户的邮箱和用户ID。
 * 验证成功后，返回包含用户邮箱和用户ID的Pair对象；如果验证失败，则返回null。
 * 该类依赖于Google的NetHttpTransport和JacksonFactory来构建ID令牌验证器。
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>验证Google ID令牌的有效性</li>
 *   <li>提取并返回用户的邮箱和用户ID</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * {@code
 * GoogleIdTokenService service = new GoogleIdTokenService();
 * Pair<String, String> userInfo = service.verifyGoogleIdToken(jwtToken, clientIds);
 * if (userInfo != null) {
 *     String email = userInfo.getLeft();
 *     String userId = userInfo.getRight();
 *     // 处理用户信息
 * }
 * }
 * </pre>
 *
 * <p>注意：该类依赖于外部库和服务，因此在使用前需要确保相关依赖已正确配置。
 * </p>
 */

public class GoogleIdTokenService {


    /**
     * 验证Google ID令牌并提取用户信息
     *
     * @param jwtToken  要验证的JWT令牌
     * @param clientIds 允许的客户端ID列表
     * @return 包含用户邮箱和用户ID的Pair，如果验证失败则返回null
     */
    public Pair<String, String> verifyGoogleIdToken(String jwtToken, List<String> clientIds) {
        log.info("jwtToken:{} ,clientIds:{} ", jwtToken, clientIds);
        if (jwtToken == null || clientIds == null || clientIds.isEmpty()) {
            log.error("jwtToken or clientIds empty");
            return null;
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(clientIds)
                    //.setIssuer(ISSUER)
                    .build();
            GoogleIdToken idToken = verifier.verify(jwtToken);
            if (idToken != null) {
                log.info("idToken:{}", idToken);
                Payload payload = idToken.getPayload();

                // 获取用户ID
                String userId = payload.getSubject();
                String email = payload.getEmail();
                log.info("userId:{}", userId);
                return Pair.of(email, userId);
            } else {
                log.error("Invalid ID token:{}", jwtToken);
                return null;
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("verifyGoogleIdToken error:{}", e);
            return null;
        }
    }

}
