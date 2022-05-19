package com.xiaomi.data.push.graph;

/**
 * Created by zhangzhiyong on 08/06/2018.
 */
public class Vertex<D> {


    private int v;

    private D data;

    public Vertex(int v, D data) {
        this.v = v;
        this.data = data;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
