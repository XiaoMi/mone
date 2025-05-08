package run.mone.agentx.dto;

import lombok.Data;
import run.mone.agentx.entity.User;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Long ctime;
    private Long utime;
    private Integer state;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCtime(user.getCtime());
        dto.setUtime(user.getUtime());
        dto.setState(user.getState());
        return dto;
    }
} 