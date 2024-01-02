package run.mone.mimeter.dashboard.bo.sla;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmDto implements Serializable {

    @HttpApiDocClassDefine(value = "alarmType", required = false, description = "报警类别", defaultValue = "")
    private String alarmType;

    @HttpApiDocClassDefine(value = "alarmMethods", required = false, description = "报警方式 0:飞书 1:短信", defaultValue = "")
    private List<Integer> alarmMethods;

    private List<UserDTO> usernames;
}
