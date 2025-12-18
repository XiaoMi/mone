/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.configs.Const;

import java.util.HashMap;
import java.util.Map;

/**
 * User information extracted from token validation.
 * Provides a strongly-typed alternative to Map&lt;String, Object&gt;.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * User ID
     */
    private String userId;

    /**
     * Username
     */
    private String username;

    /**
     * Client ID
     */
    private String clientId;

    /**
     * Creates UserInfo from a Map. Useful for backward compatibility.
     * @param map The map containing user info fields
     * @return UserInfo instance
     */
    public static UserInfo fromMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return new UserInfo();
        }

        UserInfo userInfo = new UserInfo();

        Object userId = map.get(Const.TOKEN_USER_ID);
        if (userId != null) {
            userInfo.setUserId(userId.toString());
        }

        Object username = map.get(Const.TOKEN_USERNAME);
        if (username != null) {
            userInfo.setUsername(username.toString());
        }

        Object clientId = map.get(Const.CLIENT_ID);
        if (clientId != null) {
            userInfo.setClientId(clientId.toString());
        }

        return userInfo;
    }

    /**
     * Converts UserInfo to a Map. Useful for backward compatibility.
     * @return Map representation of user info
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (userId != null) {
            map.put(Const.TOKEN_USER_ID, userId);
        }
        if (username != null) {
            map.put(Const.TOKEN_USERNAME, username);
        }
        if (clientId != null) {
            map.put(Const.CLIENT_ID, clientId);
        }
        return map;
    }

    /**
     * Checks if the UserInfo is empty (all fields are null).
     * @return true if all fields are null, false otherwise
     */
    public boolean isEmpty() {
        return userId == null && username == null && clientId == null;
    }
}
