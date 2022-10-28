package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.CompileRecord;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @author tsingfu
 */
@Service
public class CompilationService {

    @Reference(group = "${ref.gwdash.service.group}", interfaceClass = ICompilationService.class, check = false)
    private ICompilationService gwdashCompilationService;

    public long startCompile(CompileParam compileParam) {
        return gwdashCompilationService.startCompile(compileParam);
    }

    public CompileRecord getCompileRecord(long id) {
        return gwdashCompilationService.getCompileRecord(id);
    }

}
