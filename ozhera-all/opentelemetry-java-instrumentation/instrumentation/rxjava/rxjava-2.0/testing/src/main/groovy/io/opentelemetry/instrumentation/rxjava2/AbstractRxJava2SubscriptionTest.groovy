/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rxjava2

import static io.opentelemetry.instrumentation.test.utils.TraceUtils.basicSpan
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runInternalSpan
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderTrace

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.instrumentation.test.InstrumentationSpecification
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer

import java.util.concurrent.CountDownLatch

abstract class AbstractRxJava2SubscriptionTest extends InstrumentationSpecification {

  def "subscribe single test"() {
    when:
    CountDownLatch latch = new CountDownLatch(1)
    runUnderTrace("parent") {
      Single<Connection> connection = Single.create {
        it.onSuccess(new Connection())
      }
      connection.subscribe(new Consumer<Connection>() {
        @Override
        void accept(Connection t) {
          t.query()
          latch.countDown()
        }
      })
    }
    latch.await()

    then:
    assertTraces(1) {
      trace(0, 2) {
        basicSpan(it, 0, "parent")
        basicSpan(it, 1, "Connection.query", span(0))
      }
    }
  }

  def "test observable fusion"() {
    when:
    CountDownLatch latch = new CountDownLatch(1)
    runUnderTrace("parent") {
      Observable<Integer> integerObservable = Observable.just(1, 2, 3, 4)
      integerObservable.concatMap({
        return Observable.just(it)
      }).count().subscribe(new Consumer<Long>() {
        @Override
        void accept(Long count) {
          runInternalSpan("child")
          latch.countDown()
        }
      })
    }
    latch.await()

    then:
    assertTraces(1) {
      trace(0, 2) {
        basicSpan(it, 0, "parent")
        basicSpan(it, 1, "child", span(0))
      }
    }
  }

  static class Connection {
    static int query() {
      def span = GlobalOpenTelemetry.getTracer("test").spanBuilder("Connection.query").startSpan()
      span.end()
      return new Random().nextInt()
    }
  }
}
