package com.example.javaagent;

import com.google.auto.service.AutoService;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurablePropagatorProvider;

/**
 * Registers the custom propagator used by this example.
 *
 * @see ConfigurablePropagatorProvider
 * @see DemoPropagator
 */
@AutoService(ConfigurablePropagatorProvider.class)
public class DemoPropagatorProvider implements ConfigurablePropagatorProvider {
  @Override
  public TextMapPropagator getPropagator() {
    return new DemoPropagator();
  }

  @Override
  public String getName() {
    return "demo";
  }
}
