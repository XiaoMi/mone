//package run.mone.local.docean.service;
//
//import com.google.common.base.Preconditions;
//import com.google.gson.JsonObject;
//import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
//import com.xiaomi.youpin.docean.anno.Service;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import run.mone.local.docean.util.GsonUtils;
//import run.mone.m78.api.BotPluginProvider;
//import run.mone.m78.api.CodeProvider;
//import run.mone.m78.api.bo.code.CodeDTO;
//import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
//
///**
// * @author HawickMason@xiaomi.com
// * @date 3/11/24 15:02
// */
//@Slf4j
//@Service
//public class CodeService {
//
//    @Reference(interfaceClass = CodeProvider.class, group = "staging", version = "1.0", timeout = 30000, check = false)
//    private CodeProvider codeProvider;
//
//    public String getCodeDetailById(Long id) {
//        Preconditions.checkArgument(id != null, "须传递id");
//        Result<CodeDTO> res = codeProvider.getCodeDetailById(id);
//        log.info("getCodeDetail, id:{}, res:{}", id, res);
//        if (res == null || res.getCode() != GeneralCodes.OK.getCode() || res.getData() == null) {
//            return "";
//        }
//        CodeDTO data = res.getData();
//        CodeDTO.CodeContentDTO code = data.getCode();
//        if (code == null || StringUtils.isBlank(code.getCode())) {
//            return "";
//        }
//        return code.getCode();
//    }
//}
