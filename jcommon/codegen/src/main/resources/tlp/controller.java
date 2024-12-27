package run.mone.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.vo.${className}VO;
import run.mone.model.po.${className};
import run.mone.model.transfer.${className}Transfer;
import run.mone.service.${className}Service;

import javax.annotation.Resource;

/**
 * @author ${author}
 */
@HttpApiModule(value = "${className}Controller", apiController = ${className}Controller.class)
@Controller
@RequestMapping(path = "/api/${strutil.toLowerCase(className)}")
@Slf4j
public class ${className}Controller extends MongodbController<${className}> {

    @Resource
    private ${className}Service ${strutil.toLowerCase(className)}Service;

    public ${className}Controller() {
        super(${className}.class);
    }


    @RequestMapping(path = "/create", method = "post")
    @HttpApiDoc(value = "/api/${strutil.toLowerCase(className)}/create", method = MiApiRequestMethod.POST, apiName = "创建")
    public Result<Boolean> create${className}(${className}VO ${strutil.toLowerCase(className)}VO) {
        ${className} ${strutil.toLowerCase(className)} = ${className}Transfer.vo2po(${strutil.toLowerCase(className)}VO);
        boolean result = ${strutil.toLowerCase(className)}Service.save(${strutil.toLowerCase(className)});
        return Result.success(result);
    }
}