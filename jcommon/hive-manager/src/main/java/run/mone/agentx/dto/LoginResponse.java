package run.mone.agentx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.agentx.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String username;
    private String token;
    
    public static LoginResponse fromUser(User user, String token) {
        return new LoginResponse(user.getId(), user.getUsername(), token);
    }
} 