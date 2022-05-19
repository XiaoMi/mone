package com.xiaomi.data.push.antlr.sql.util;

public class Tuple3<A, B, C> {
    final A a;
    final B b;
    final C c;

    public Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A _1() {
        return this.a;
    }

    public B _2() {
        return this.b;
    }

    public C _3() {
        return this.c;
    }
}
