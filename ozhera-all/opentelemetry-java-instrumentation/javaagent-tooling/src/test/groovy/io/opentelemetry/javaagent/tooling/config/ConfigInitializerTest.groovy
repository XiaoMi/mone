/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.config


import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.RestoreSystemProperties
import spock.lang.Specification

class ConfigInitializerTest extends Specification {
  @Rule
  public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties()
  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables()

  def "should use SPI properties"() {
    given:
    def spiConfiguration = new Properties()
    spiConfiguration.put("property1", "spi-1")
    spiConfiguration.put("property2", "spi-2")
    spiConfiguration.put("property3", "spi-3")
    spiConfiguration.put("property4", "spi-4")

    when:
    def config = ConfigInitializer.create(spiConfiguration, new Properties())

    then:
    config.getProperty("property1") == "spi-1"
    config.getProperty("property2") == "spi-2"
    config.getProperty("property3") == "spi-3"
    config.getProperty("property4") == "spi-4"
  }

  def "should use configuration file properties (takes precedence over SPI)"() {
    given:
    def spiConfiguration = new Properties()
    spiConfiguration.put("property1", "spi-1")
    spiConfiguration.put("property2", "spi-2")
    spiConfiguration.put("property3", "spi-3")
    spiConfiguration.put("property4", "spi-4")

    def configurationFile = new Properties()
    configurationFile.put("property1", "cf-1")
    configurationFile.put("property2", "cf-2")
    configurationFile.put("property3", "cf-3")

    when:
    def config = ConfigInitializer.create(spiConfiguration, configurationFile)

    then:
    config.getProperty("property1") == "cf-1"
    config.getProperty("property2") == "cf-2"
    config.getProperty("property3") == "cf-3"
    config.getProperty("property4") == "spi-4"
  }

  def "should use environment variables (takes precedence over configuration file)"() {
    given:
    def spiConfiguration = new Properties()
    spiConfiguration.put("property1", "spi-1")
    spiConfiguration.put("property2", "spi-2")
    spiConfiguration.put("property3", "spi-3")
    spiConfiguration.put("property4", "spi-4")

    def configurationFile = new Properties()
    configurationFile.put("property1", "cf-1")
    configurationFile.put("property2", "cf-2")
    configurationFile.put("property3", "cf-3")

    environmentVariables.set("property1", "env-1")
    environmentVariables.set("property2", "env-2")

    when:
    def config = ConfigInitializer.create(spiConfiguration, configurationFile)

    then:
    config.getProperty("property1") == "env-1"
    config.getProperty("property2") == "env-2"
    config.getProperty("property3") == "cf-3"
    config.getProperty("property4") == "spi-4"
  }

  def "should use system properties (takes precedence over environment variables)"() {
    given:
    def spiConfiguration = new Properties()
    spiConfiguration.put("property1", "spi-1")
    spiConfiguration.put("property2", "spi-2")
    spiConfiguration.put("property3", "spi-3")
    spiConfiguration.put("property4", "spi-4")

    def configurationFile = new Properties()
    configurationFile.put("property1", "cf-1")
    configurationFile.put("property2", "cf-2")
    configurationFile.put("property3", "cf-3")

    environmentVariables.set("property1", "env-1")
    environmentVariables.set("property2", "env-2")

    System.setProperty("property1", "sp-1")

    when:
    def config = ConfigInitializer.create(spiConfiguration, configurationFile)

    then:
    config.getProperty("property1") == "sp-1"
    config.getProperty("property2") == "env-2"
    config.getProperty("property3") == "cf-3"
    config.getProperty("property4") == "spi-4"
  }

  def "should normalize property names"() {
    given:
    def spiConfiguration = new Properties()
    spiConfiguration.put("otel.some-property.from-spi", "value")

    def configurationFile = new Properties()
    configurationFile.put("otel.some-property.from-file", "value")

    environmentVariables.set("OTEL_SOME_ENV_VAR", "value")

    System.setProperty("otel.some-system-property", "value")

    when:
    def config = ConfigInitializer.create(spiConfiguration, configurationFile)

    then:
    config.getProperty("otel.some-property.from-spi") == "value"
    config.getProperty("otel.some-property.from-file") == "value"
    config.getProperty("otel.some-env-var") == "value"
    config.getProperty("otel.some-system-property") == "value"
  }
}
