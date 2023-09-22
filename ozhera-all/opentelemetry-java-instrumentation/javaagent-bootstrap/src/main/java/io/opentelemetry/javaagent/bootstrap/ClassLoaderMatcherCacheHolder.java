/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.bootstrap;

import io.opentelemetry.instrumentation.api.caching.Cache;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.lock.qual.GuardedBy;

/**
 * A holder of all ClassLoaderMatcher caches. We store them in the bootstrap classloader so that
 * instrumentation can invalidate the ClassLoaderMatcher for a particular ClassLoader, e.g. when
 * {@link java.net.URLClassLoader#addURL(URL)} is called.
 */
public class ClassLoaderMatcherCacheHolder {

  @GuardedBy("allCaches")
  private static final List<Cache<ClassLoader, Boolean>> allCaches = new ArrayList<>();

  private ClassLoaderMatcherCacheHolder() {}

  public static void addCache(Cache<ClassLoader, Boolean> cache) {
    synchronized (allCaches) {
      allCaches.add(cache);
    }
  }

  public static void invalidateAllCachesForClassLoader(ClassLoader loader) {
    synchronized (allCaches) {
      for (Cache<ClassLoader, Boolean> cache : allCaches) {
        cache.remove(loader);
      }
    }
  }
}
