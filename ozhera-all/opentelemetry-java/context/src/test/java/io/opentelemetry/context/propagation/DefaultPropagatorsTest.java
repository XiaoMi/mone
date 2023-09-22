/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context.propagation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DefaultContextPropagators}. */
class DefaultPropagatorsTest {

  @Test
  void addTextMapPropagatorNull() {
    assertThatThrownBy(() -> ContextPropagators.create(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void testInject() {
    CustomTextMapPropagator propagator1 = new CustomTextMapPropagator("prop1");
    CustomTextMapPropagator propagator2 = new CustomTextMapPropagator("prop2");
    ContextPropagators propagators =
        ContextPropagators.create(TextMapPropagator.composite(propagator1, propagator2));

    Context context = Context.current();
    context = context.with(propagator1.getKey(), "value1");
    context = context.with(propagator2.getKey(), "value2");

    Map<String, String> map = new HashMap<>();
    propagators.getTextMapPropagator().inject(context, map, MapSetter.INSTANCE);
    assertThat(map.get(propagator1.getKeyName())).isEqualTo("value1");
    assertThat(map.get(propagator2.getKeyName())).isEqualTo("value2");
  }

  @Test
  void testExtract() {
    CustomTextMapPropagator propagator1 = new CustomTextMapPropagator("prop1");
    CustomTextMapPropagator propagator2 = new CustomTextMapPropagator("prop2");
    CustomTextMapPropagator propagator3 = new CustomTextMapPropagator("prop3");
    ContextPropagators propagators =
        ContextPropagators.create(TextMapPropagator.composite(propagator1, propagator2));

    // Put values for propagators 1 and 2 only.
    Map<String, String> map = new HashMap<>();
    map.put(propagator1.getKeyName(), "value1");
    map.put(propagator2.getKeyName(), "value2");

    Context context =
        propagators.getTextMapPropagator().extract(Context.current(), map, MapGetter.INSTANCE);
    assertThat(context.get(propagator1.getKey())).isEqualTo("value1");
    assertThat(context.get(propagator2.getKey())).isEqualTo("value2");
    assertThat(context.get(propagator3.getKey())).isNull(); // Handle missing value.
  }

  @Test
  public void testDuplicatedFields() {
    CustomTextMapPropagator propagator1 = new CustomTextMapPropagator("prop1");
    CustomTextMapPropagator propagator2 = new CustomTextMapPropagator("prop2");
    CustomTextMapPropagator propagator3 = new CustomTextMapPropagator("prop1");
    CustomTextMapPropagator propagator4 = new CustomTextMapPropagator("prop2");
    ContextPropagators propagators =
        ContextPropagators.create(
            TextMapPropagator.composite(propagator1, propagator2, propagator3, propagator4));

    Collection<String> fields = propagators.getTextMapPropagator().fields();
    assertThat(fields).containsExactly("prop1", "prop2");
  }

  @Test
  void noopPropagator() {
    ContextPropagators propagators = ContextPropagators.noop();

    Context context = Context.current();
    Map<String, String> map = new HashMap<>();
    propagators.getTextMapPropagator().inject(context, map, MapSetter.INSTANCE);
    assertThat(map).isEmpty();

    assertThat(propagators.getTextMapPropagator().extract(context, map, MapGetter.INSTANCE))
        .isSameAs(context);
  }

  private static class CustomTextMapPropagator implements TextMapPropagator {
    private final String name;
    private final ContextKey<String> key;

    CustomTextMapPropagator(String name) {
      this.name = name;
      this.key = ContextKey.named(name);
    }

    ContextKey<String> getKey() {
      return key;
    }

    String getKeyName() {
      return name;
    }

    @Override
    public Collection<String> fields() {
      return Collections.singletonList(name);
    }

    @Override
    public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
      Object payload = context.get(key);
      if (payload != null) {
        setter.set(carrier, name, payload.toString());
      }
    }

    @Override
    public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
      String payload = getter.get(carrier, name);
      if (payload != null) {
        context = context.with(key, payload);
      }

      return context;
    }
  }

  private static final class MapSetter implements TextMapSetter<Map<String, String>> {
    private static final MapSetter INSTANCE = new MapSetter();

    @Override
    public void set(Map<String, String> map, String key, String value) {
      map.put(key, value);
    }

    private MapSetter() {}
  }

  private static final class MapGetter implements TextMapGetter<Map<String, String>> {
    private static final MapGetter INSTANCE = new MapGetter();

    @Override
    public Iterable<String> keys(Map<String, String> map) {
      return map.keySet();
    }

    @Override
    public String get(Map<String, String> map, String key) {
      return map.get(key);
    }

    private MapGetter() {}
  }
}
