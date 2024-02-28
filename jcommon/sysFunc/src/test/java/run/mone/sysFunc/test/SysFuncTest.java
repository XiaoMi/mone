package run.mone.sysFunc.test;

import org.junit.Ignore;
import org.junit.Test;
import run.mone.sysFunc.SysFuncUtils;

@Ignore
public class SysFuncTest {

    @Test
    public void testSubstring() {
        String res = SysFuncUtils.gen("${java.substring(ceshi, 1, 3)}");
        System.out.println(res);
    }

    @Test
    public void testUuid() {
        String res = SysFuncUtils.gen("${java.uuid()}");
        System.out.println(res);
    }

    @Test
    public void testRandomNumber() {
        String res = SysFuncUtils.gen("${java.randomNumber(2,11)}");
        System.out.println(res);
    }



}
