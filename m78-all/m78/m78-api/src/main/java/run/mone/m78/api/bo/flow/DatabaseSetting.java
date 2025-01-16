package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/27/24 20:03
 */
@Data
public class DatabaseSetting implements Serializable {

    private static final long serialVersionUID = 2911272348580476282L;

    private String sql;
}
