package run.mone.neo4j.test.m;

import run.mone.neo4j.test.anno.Table;

/**
 * @author goodjava@qq.com
 * @date 2024/8/19 17:03
 */
@Table
public class Cat {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
