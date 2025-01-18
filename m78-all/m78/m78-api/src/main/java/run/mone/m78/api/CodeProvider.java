package run.mone.m78.api;

import com.xiaomi.youpin.infra.rpc.Result;
import run.mone.m78.api.bo.code.CodeDTO;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/11/24 14:48
 */
public interface CodeProvider {

    Result<CodeDTO>  getCodeDetailById(Long id);

}
