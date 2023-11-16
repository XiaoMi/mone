package io.opentelemetry.exporter.logging;


import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SystemOut")
public class LogbackSpanExporterTest {

  @Test
  public void test1(){
      String a = "a ### b ###  ###  ### c ### ";
      String[] split = a.split(" ### ");
      System.out.println(split.length);
  }

  @Test
  public void test2(){
    Map<String,String> map = new HashMap<>();
    map.forEach((key,value) -> System.out.println(key+":"+value));
  }

  @Test
  public void test3(){
    StringBuilder sb = new StringBuilder();
    sb.append("a,").append("b,").append("c,").append("d,");
    String substring = sb.substring(0, sb.length() - 1);
    sb.deleteCharAt(sb.length()-1);
    System.out.println(substring);
  }

  @Test
  public void test4(){
    String a = "123_sdada_sds";
    int i = a.indexOf("_");
    String pre = a.substring(0,i);

    System.out.println(pre);
    String substring = a.substring(i+1);

    System.out.println(substring);
  }

  @Test
  public void test5(){
    String a = "|a||";
    String[] split = a.split("\\|");
    System.out.println(split.length);
  }
}
