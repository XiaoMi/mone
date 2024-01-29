package run.mone.ultraman.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/4/19 14:44
 */
public class Param implements Serializable {

    private Gson gson = new Gson();

    private String cmd;

    private String param;

    private String path;

    private String v = "";

    public void decode(FullHttpRequest req) {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        this.path = decoder.path().replaceAll("/", "");
        List<String> cmdList = decoder.parameters().get("cmd");
        if (null != cmdList && cmdList.size() > 0) {
            this.cmd = cmdList.get(0);
        }
        List<String> vList = decoder.parameters().get("v");
        if (null != vList && vList.size() > 0) {
            this.v = vList.get(0);
        }
        List<String> paramList = decoder.parameters().get("param");
        if (null != paramList && paramList.size() > 0) {
            this.param = paramList.get(0);
        }
    }

    public Pair<String, String> getPathAndParam(String res) {
        String param = "";
        String path = "";
        try {
            int i = res.indexOf("{");
            if (i > 0) {
                res = res.substring(i);
            }
            JsonObject obj = gson.fromJson(res, JsonObject.class);
            path = obj.get("cmd2").getAsString();
            if (null != obj.get("param")) {
                param = obj.get("param").getAsString();
            }
            return Pair.of(path, param);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return Pair.of(path, param);
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
