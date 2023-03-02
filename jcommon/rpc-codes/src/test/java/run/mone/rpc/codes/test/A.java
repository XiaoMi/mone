package run.mone.rpc.codes.test;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/2/14 17:46
 */
@Data
public class A implements Serializable {

    private int id;

    private char c;

    private short s;

    private Date date;

    private String name;

    private B b;

    private List<String> list;

    private Map<String, Integer> map;

    private Float f;

    private long l;

    private byte by;

    private double dou;

    private int[] array;
}
