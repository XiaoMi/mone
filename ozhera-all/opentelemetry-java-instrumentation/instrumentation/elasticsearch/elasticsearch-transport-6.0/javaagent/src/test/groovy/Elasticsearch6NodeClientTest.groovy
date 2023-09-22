/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT
import static io.opentelemetry.api.trace.StatusCode.ERROR
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderTrace
import static org.elasticsearch.cluster.ClusterName.CLUSTER_NAME_SETTING

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest
import org.elasticsearch.common.io.FileSystemUtils
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.index.IndexNotFoundException
import org.elasticsearch.node.Node
import spock.lang.Shared

class Elasticsearch6NodeClientTest extends AgentInstrumentationSpecification {
  public static final long TIMEOUT = 10000 // 10 seconds

  @Shared
  Node testNode
  @Shared
  File esWorkingDir
  @Shared
  String clusterName = UUID.randomUUID().toString()

  def client = testNode.client()

  def setupSpec() {

    esWorkingDir = File.createTempDir("test-es-working-dir-", "")
    esWorkingDir.deleteOnExit()
    println "ES work dir: $esWorkingDir"

    def settings = Settings.builder()
      .put("path.home", esWorkingDir.path)
    // Since we use listeners to close spans this should make our span closing deterministic which is good for tests
      .put("thread_pool.listener.size", 1)
      .put(CLUSTER_NAME_SETTING.getKey(), clusterName)
      .put("discovery.type", "single-node")
      .build()
    testNode = NodeFactory.newNode(settings)
    testNode.start()
    runUnderTrace("setup") {
      // this may potentially create multiple requests and therefore multiple spans, so we wrap this call
      // into a top level trace to get exactly one trace in the result.
      testNode.client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(TIMEOUT)
      // disable periodic refresh in InternalClusterInfoService as it creates spans that tests don't expect
      testNode.client().admin().cluster().updateSettings(new ClusterUpdateSettingsRequest().transientSettings(["cluster.routing.allocation.disk.threshold_enabled": false]))
    }
    waitForTraces(1)
  }

  def cleanupSpec() {
    testNode?.close()
    if (esWorkingDir != null) {
      FileSystemUtils.deleteSubDirectories(esWorkingDir.toPath())
      esWorkingDir.delete()
    }
  }

  def "test elasticsearch status"() {
    setup:
    def result = client.admin().cluster().health(new ClusterHealthRequest()).get()

    def clusterHealthStatus = result.status

    expect:
    clusterHealthStatus.name() == "GREEN"

    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          name "ClusterHealthAction"
          kind CLIENT
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "ClusterHealthAction"
            "elasticsearch.action" "ClusterHealthAction"
            "elasticsearch.request" "ClusterHealthRequest"
          }
        }
      }
    }
  }

  def "test elasticsearch error"() {
    when:
    client.prepareGet(indexName, indexType, id).get()

    then:
    thrown IndexNotFoundException

    and:
    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          name "GetAction"
          kind CLIENT
          status ERROR
          errorEvent IndexNotFoundException, ~/no such index( \[invalid-index])?/
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "GetAction"
            "elasticsearch.action" "GetAction"
            "elasticsearch.request" "GetRequest"
            "elasticsearch.request.indices" indexName
          }
        }
      }
    }

    where:
    indexName = "invalid-index"
    indexType = "test-type"
    id = "1"
  }

  def "test elasticsearch get"() {
    setup:
    def indexResult = client.admin().indices().prepareCreate(indexName).get()

    expect:
    indexResult.index() == indexName

    when:
    def emptyResult = client.prepareGet(indexName, indexType, id).get()

    then:
    !emptyResult.isExists()
    emptyResult.id == id
    emptyResult.type == indexType
    emptyResult.index == indexName

    when:
    def createResult = client.prepareIndex(indexName, indexType, id).setSource([:]).get()

    then:
    createResult.id == id
    createResult.type == indexType
    createResult.index == indexName
    createResult.status().status == 201

    when:
    def result = client.prepareGet(indexName, indexType, id).get()

    then:
    result.isExists()
    result.id == id
    result.type == indexType
    result.index == indexName

    and:
    assertTraces(4) {
      trace(0, 1) {
        span(0) {
          name "CreateIndexAction"
          kind CLIENT
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "CreateIndexAction"
            "elasticsearch.action" "CreateIndexAction"
            "elasticsearch.request" "CreateIndexRequest"
            "elasticsearch.request.indices" indexName
          }
        }
      }
      trace(1, 1) {
        span(0) {
          name "GetAction"
          kind CLIENT
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "GetAction"
            "elasticsearch.action" "GetAction"
            "elasticsearch.request" "GetRequest"
            "elasticsearch.request.indices" indexName
            "elasticsearch.type" indexType
            "elasticsearch.id" "1"
            "elasticsearch.version"(-1)
          }
        }
      }
      trace(2, 2) {
        span(0) {
          name "IndexAction"
          kind CLIENT
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "IndexAction"
            "elasticsearch.action" "IndexAction"
            "elasticsearch.request" "IndexRequest"
            "elasticsearch.request.indices" indexName
            "elasticsearch.request.write.type" indexType
            "elasticsearch.request.write.version"(-3)
            "elasticsearch.response.status" 201
            "elasticsearch.shard.replication.total" 2
            "elasticsearch.shard.replication.successful" 1
            "elasticsearch.shard.replication.failed" 0
          }
        }
        span(1) {
          name ~/(Auto)?PutMappingAction/
          kind CLIENT
          childOf span(0)
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" ~/(Auto)?PutMappingAction/
            "elasticsearch.action" ~/(Auto)?PutMappingAction/
            "elasticsearch.request" "PutMappingRequest"
          }
        }
      }
      trace(3, 1) {
        span(0) {
          name "GetAction"
          kind CLIENT
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "GetAction"
            "elasticsearch.action" "GetAction"
            "elasticsearch.request" "GetRequest"
            "elasticsearch.request.indices" indexName
            "elasticsearch.type" indexType
            "elasticsearch.id" "1"
            "elasticsearch.version" 1
          }
        }
      }
    }

    cleanup:
    client.admin().indices().prepareDelete(indexName).get()

    where:
    indexName = "test-index"
    indexType = "test-type"
    id = "1"
  }
}
