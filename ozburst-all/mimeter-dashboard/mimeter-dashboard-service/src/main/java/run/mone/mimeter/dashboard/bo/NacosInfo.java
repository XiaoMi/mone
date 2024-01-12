package run.mone.mimeter.dashboard.bo;

import lombok.Data;

@Data
public class NacosInfo {
    private String usernameSt;
    private String passwordSt;

    private String usernameOl;
    private String passwordOl;


    public NacosInfo(String usernameSt, String passwordSt, String usernameOl, String passwordOl) {
        this.usernameSt = usernameSt;
        this.passwordSt = passwordSt;
        this.usernameOl = usernameOl;
        this.passwordOl = passwordOl;
    }
}
