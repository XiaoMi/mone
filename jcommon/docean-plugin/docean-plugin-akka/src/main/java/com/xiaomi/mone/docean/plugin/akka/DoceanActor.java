package com.xiaomi.mone.docean.plugin.akka;

import akka.actor.AbstractActor;

/**
 * @author goodjava@qq.com
 * @date 1/30/21
 */
public class DoceanActor extends AbstractActor {

    private Receive receive;

    public DoceanActor(Receive receive) {
        this.receive = receive;
    }

    @Override
    public Receive createReceive() {
        return this.receive;
    }
}
