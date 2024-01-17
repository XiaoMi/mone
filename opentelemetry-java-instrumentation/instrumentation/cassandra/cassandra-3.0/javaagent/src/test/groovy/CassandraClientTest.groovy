/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.basicSpan
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderTrace

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import spock.lang.Shared

class CassandraClientTest extends AgentInstrumentationSpecification {
  private static final Logger log = LoggerFactory.getLogger(CassandraClientTest)

  @Shared
  def executor = Executors.newCachedThreadPool()

  @Shared
  GenericContainer cassandra
  @Shared
  int cassandraPort
  @Shared
  Cluster cluster

  def setupSpec() {
    cassandra = new GenericContainer("cassandra:3")
      .withExposedPorts(9042)
      .withLogConsumer(new Slf4jLogConsumer(log))
      .withStartupTimeout(Duration.ofSeconds(120))
    cassandra.start()

    cassandraPort = cassandra.getMappedPort(9042)

    cluster = Cluster.builder()
      .addContactPointsWithPorts(new InetSocketAddress("localhost", cassandraPort))
      .build()
  }

  def cleanupSpec() {
    cluster?.close()
    cassandra.stop()
  }

  def "test sync"() {
    setup:
    Session session = cluster.connect(keyspace)

    session.execute(statement)

    expect:
    assertTraces(keyspace ? 2 : 1) {
      if (keyspace) {
        trace(0, 1) {
          cassandraSpan(it, 0, "DB Query", "USE $keyspace")
        }
      }
      trace(keyspace ? 1 : 0, 1) {
        cassandraSpan(it, 0, spanName, expectedStatement, operation, keyspace, table)
      }
    }

    cleanup:
    session.close()

    where:
    keyspace    | statement                                                                                         | expectedStatement                                                 | spanName                 | operation | table
    null        | "DROP KEYSPACE IF EXISTS sync_test"                                                               | "DROP KEYSPACE IF EXISTS sync_test"                               | "DB Query"               | null      | null
    null        | "CREATE KEYSPACE sync_test WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':3}" | "CREATE KEYSPACE sync_test WITH REPLICATION = {?:?, ?:?}"         | "DB Query"               | null      | null
    "sync_test" | "CREATE TABLE sync_test.users ( id UUID PRIMARY KEY, name text )"                                 | "CREATE TABLE sync_test.users ( id UUID PRIMARY KEY, name text )" | "sync_test"              | null      | null
    "sync_test" | "INSERT INTO sync_test.users (id, name) values (uuid(), 'alice')"                                 | "INSERT INTO sync_test.users (id, name) values (uuid(), ?)"       | "INSERT sync_test.users" | "INSERT"  | "sync_test.users"
    "sync_test" | "SELECT * FROM users where name = 'alice' ALLOW FILTERING"                                        | "SELECT * FROM users where name = ? ALLOW FILTERING"              | "SELECT sync_test.users" | "SELECT"  | "users"
  }

  def "test async"() {
    setup:
    def callbackExecuted = new AtomicBoolean()
    Session session = cluster.connect(keyspace)
    runUnderTrace("parent") {
      def future = session.executeAsync(statement)
      future.addListener({ ->
        runUnderTrace("callbackListener") {
          callbackExecuted.set(true)
        }
      }, executor)
    }

    expect:
    assertTraces(keyspace ? 2 : 1) {
      if (keyspace) {
        trace(0, 1) {
          cassandraSpan(it, 0, "DB Query", "USE $keyspace")
        }
      }
      trace(keyspace ? 1 : 0, 3) {
        basicSpan(it, 0, "parent")
        cassandraSpan(it, 1, spanName, expectedStatement, operation, keyspace, table, span(0))
        basicSpan(it, 2, "callbackListener", span(0))
      }
    }

    cleanup:
    session.close()

    where:
    keyspace     | statement                                                                                          | expectedStatement                                                  | spanName                  | operation | table
    null         | "DROP KEYSPACE IF EXISTS async_test"                                                               | "DROP KEYSPACE IF EXISTS async_test"                               | "DB Query"                | null      | null
    null         | "CREATE KEYSPACE async_test WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':3}" | "CREATE KEYSPACE async_test WITH REPLICATION = {?:?, ?:?}"         | "DB Query"                | null      | null
    "async_test" | "CREATE TABLE async_test.users ( id UUID PRIMARY KEY, name text )"                                 | "CREATE TABLE async_test.users ( id UUID PRIMARY KEY, name text )" | "async_test"              | null      | null
    "async_test" | "INSERT INTO async_test.users (id, name) values (uuid(), 'alice')"                                 | "INSERT INTO async_test.users (id, name) values (uuid(), ?)"       | "INSERT async_test.users" | "INSERT"  | "async_test.users"
    "async_test" | "SELECT * FROM users where name = 'alice' ALLOW FILTERING"                                         | "SELECT * FROM users where name = ? ALLOW FILTERING"               | "SELECT async_test.users" | "SELECT"  | "users"
  }

  def cassandraSpan(TraceAssert trace, int index, String spanName, String statement,
                    String operation = null,
                    String keyspace = null,
                    String table = null,
                    Object parentSpan = null) {
    trace.span(index) {
      name spanName
      kind CLIENT
      if (parentSpan == null) {
        hasNoParent()
      } else {
        childOf((SpanData) parentSpan)
      }
      attributes {
        "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
        "$SemanticAttributes.NET_PEER_IP.key" "127.0.0.1"
        "$SemanticAttributes.NET_PEER_PORT.key" cassandraPort
        "$SemanticAttributes.DB_SYSTEM.key" "cassandra"
        "$SemanticAttributes.DB_NAME.key" keyspace
        "$SemanticAttributes.DB_STATEMENT.key" statement
        "$SemanticAttributes.DB_OPERATION.key" operation
        "$SemanticAttributes.DB_CASSANDRA_KEYSPACE.key" keyspace
        "$SemanticAttributes.DB_CASSANDRA_TABLE.key" table
      }
    }
  }
}
