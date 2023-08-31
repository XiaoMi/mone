package com.xiaomi.youpin.codecheck.code.impl.youpin;

import com.sun.source.tree.CompilationUnitTree;
import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.code.impl.CompilationCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @Author zhangping17
 * @Date 2022/1/16 19:30
 */
public class IPRule extends CompilationCheck {
    private static final String DESC = "file should avoid IP addresses";
    private static final String CHINA_DESC = "file should avoid IP addresses";

    @Override
    public Pair<Integer, CheckResult> _check(CompilationUnitTree compilationUnitTree) {
        List<String> ips = CommonUtils.hasIP(compilationUnitTree.toString());
        if (!ips.isEmpty()){
            return Pair.of(CheckResult.ERROR, CheckResult.getErrorRes("IPRule", DESC, CHINA_DESC+":"+String.join(",",ips)));
        } else {
            return Pair.of(CheckResult.INFO, CheckResult.getInfoRes("IPRule", "", ""));
        }
    }
}
