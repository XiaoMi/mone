/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import javax.enterprise.inject.Vetoed

// exclude this class from CDI as it causes NullPointerException when tomee is run with jdk8
@Vetoed
class TomeeRestTest extends ArquillianRestTest {
}
