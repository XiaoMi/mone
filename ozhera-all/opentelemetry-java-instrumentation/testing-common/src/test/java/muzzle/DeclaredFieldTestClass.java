/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package muzzle;

public class DeclaredFieldTestClass {
  public static class Advice {
    public void instrument() {
      new Helper().foo();
    }
  }

  public static class Helper extends LibraryBaseClass {
    private String helperField;

    public void foo() {
      superField.toString();
    }
  }

  public static class LibraryBaseClass {
    protected Object superField;
  }
}
