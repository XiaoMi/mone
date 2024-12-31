/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package springdata

import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest
import org.elasticsearch.common.io.FileSystemUtils
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.env.Environment
import org.elasticsearch.node.InternalSettingsPreparer
import org.elasticsearch.node.Node
import org.elasticsearch.transport.Netty3Plugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = "springdata")
@ComponentScan(basePackages = "springdata")
class Config {

  @Bean
  NodeBuilder nodeBuilder() {
    return new NodeBuilder()
  }

  @Bean(destroyMethod = "close")
  Node elasticSearchNode() {
    def tmpDir = File.createTempFile("test-es-working-dir-", "")
    tmpDir.delete()
    tmpDir.mkdir()
    tmpDir.deleteOnExit()

    System.addShutdownHook {
      if (tmpDir != null) {
        FileSystemUtils.deleteSubDirectories(tmpDir.toPath())
        tmpDir.delete()
      }
    }

    def settings = Settings.builder()
      .put("http.enabled", "false")
      .put("path.data", tmpDir.toString())
      .put("path.home", tmpDir.toString())
      .put("thread_pool.listener.size", 1)
      .put("transport.type", "netty3")
      .put("http.type", "netty3")
      .put("discovery.type", "single-node")
      .build()

    println "ES work dir: $tmpDir"

    def testNode = new Node(new Environment(InternalSettingsPreparer.prepareSettings(settings)), [Netty3Plugin])
    testNode.start()
    // disable periodic refresh in InternalClusterInfoService as it creates spans that tests don't expect
    testNode.client().admin().cluster().updateSettings(new ClusterUpdateSettingsRequest().transientSettings(["cluster.routing.allocation.disk.threshold_enabled": false]))

    return testNode
  }

  @Bean
  ElasticsearchOperations elasticsearchTemplate(Node node) {
    return new ElasticsearchTemplate(node.client())
  }
}
