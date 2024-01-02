package run.mone.mimeter.dashboard.bo.sla;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private String user;
    private String username;
    private String email;
    private String phoneNum;
}
