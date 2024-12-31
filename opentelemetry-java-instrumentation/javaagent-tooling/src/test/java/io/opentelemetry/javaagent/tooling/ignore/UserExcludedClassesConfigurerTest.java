/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.ignore;

import static io.opentelemetry.javaagent.tooling.ignore.UserExcludedClassesConfigurer.EXCLUDED_CLASSES_CONFIG;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.instrumentation.api.config.ConfigBuilder;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesBuilder;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesConfigurer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserExcludedClassesConfigurerTest {
  @Mock IgnoredTypesBuilder builder;

  IgnoredTypesConfigurer underTest = new UserExcludedClassesConfigurer();

  @Test
  void shouldAddNothingToBuilderWhenPropertyIsEmpty() {
    // when
    underTest.configure(Config.create(emptyMap()), builder);

    // then
    verifyNoInteractions(builder);
  }

  @Test
  void shouldIgnoreClassesAndPackages() {
    // given
    Config config =
        new ConfigBuilder()
            .readProperties(
                singletonMap(
                    EXCLUDED_CLASSES_CONFIG,
                    "com.example.IgnoredClass,com.example.ignored.*,com.another_ignore"))
            .build();

    // when
    underTest.configure(config, builder);

    // then
    verify(builder).ignoreClass("com.example.IgnoredClass");
    verify(builder).ignoreClass("com.example.ignored.");
    verify(builder).ignoreClass("com.another_ignore");
  }
}
