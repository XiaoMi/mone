package run.mone.sysFunc.test;

import org.junit.Ignore;
import org.junit.Test;
import run.mone.sysFunc.SysFuncUtils;

import java.util.List;

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
        String res = SysFuncUtils.gen("${java.randomNumber(8000000000000,9000000000000)}");
        System.out.println(res);
    }

    @Test
    public void testRandomNumberBatch() {
        List<String> res = SysFuncUtils.batchGen("${java.randomNumber(2,11)}", 6);
        System.out.println(res);
    }

    @Test
    public void testTimeStamp() {
        String res = SysFuncUtils.gen("${java.timeStamp()}");
        System.out.println(res);
    }

    @Test
    public void testRandomString() {
        String res = SysFuncUtils.gen("${java.randomString(15)}");
        System.out.println(res);
    }

    @Test
    public void testPhoneNum() {
        String res = SysFuncUtils.gen("${java.phoneNum()}");
        System.out.println(res);
    }

    @Test
    public void testUpperCase() {
        String res = SysFuncUtils.gen("${java.upperCase(Txt Content)}");
        System.out.println(res);
    }

    @Test
    public void testLowerCase() {
        String res = SysFuncUtils.gen("${java.lowerCase(Txt Content)}");
        System.out.println(res);
    }
    @Test
    public void testSelect() {
        String res = SysFuncUtils.gen("${java.select(true,false)}");
        System.out.println(res);
    }
    @Test
    public void testRandomDouble() {
        String res = SysFuncUtils.gen("${java.randomDouble(10,50,3)}");
        System.out.println(res);
    }
    @Test
    public void testTimeStampToDate() {
        String res = SysFuncUtils.gen("${java.timeStampToDate(1711680624906,yyyy-MM-dd HH:mm:ss)}");
        System.out.println(res);
    }
    @Test
    public void testDateToTimeStamp() {
        String res = SysFuncUtils.gen("${java.dateToTimeStamp(2024-03-29 10:30:12,yyyy-MM-dd HH:mm:ss)}");
        System.out.println(res);
    }
}
