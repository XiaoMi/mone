/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.extension.muzzle;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import java.util.Objects;

/**
 * Represents the source (file name, line number) of a reference.
 *
 * <p>This class is used in the auto-generated {@link InstrumentationModule#getMuzzleReferences()}
 * method, it is not meant to be used directly by agent extension developers.
 */
public final class Source {
  private final String name;
  private final int line;

  public Source(String name, int line) {
    this.name = name;
    this.line = line;
  }

  public String getName() {
    return name;
  }

  public int getLine() {
    return line;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Source)) {
      return false;
    }
    Source other = (Source) obj;
    return name.equals(other.name) && line == other.line;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, line);
  }

  @Override
  public String toString() {
    return getName() + ":" + getLine();
  }
}
