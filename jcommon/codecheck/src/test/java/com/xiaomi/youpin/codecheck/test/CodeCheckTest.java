package com.xiaomi.youpin.codecheck.test;

import com.xiaomi.youpin.codecheck.CodeCheck;
import com.xiaomi.youpin.codecheck.DocCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CodeCheckTest {


    @Test
    public void testCode() {

        String path = "/tmp/miapimanager";

        CodeCheck codeCheck = new CodeCheck();
        Map<String, List<CheckResult>> map = codeCheck.check(path);
        map.entrySet().stream().forEach(it ->  {
            System.out.println(it.getKey());
            it.getValue().stream().forEach(it1->System.out.println(it1));
            System.out.println("\n");
        });
    }

    @Test
    public void testDoc() {

        String path = "/tmp/niuke";

        DocCheck codeCheck = new DocCheck();
        Map<String, String> map = codeCheck.getDoc(path);
        map.entrySet().stream().forEach(it ->  {
            System.out.println(it.getKey());
            System.out.println(it.getValue());
        });
    }

}
