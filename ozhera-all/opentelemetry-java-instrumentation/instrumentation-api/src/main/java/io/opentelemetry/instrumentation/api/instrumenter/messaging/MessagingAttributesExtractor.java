/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.messaging;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Extractor of <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/messaging.md">messaging
 * attributes</a>. Instrumentation of messaging frameworks/libraries should extend this class,
 * defining {@link REQUEST} and {@link RESPONSE} with the actual request / response types of the
 * instrumented library. If an attribute is not available in this library, it is appropriate to
 * return {@code null} from the protected attribute methods, but implement as many as possible for
 * best compliance with the OpenTelemetry specification.
 */
public abstract class MessagingAttributesExtractor<REQUEST, RESPONSE>
    extends AttributesExtractor<REQUEST, RESPONSE> {
  public static final String TEMP_DESTINATION_NAME = "(temporary)";

  @Override
  protected final void onStart(AttributesBuilder attributes, REQUEST request) {
    set(attributes, SemanticAttributes.MESSAGING_SYSTEM, system(request));
    set(attributes, SemanticAttributes.MESSAGING_DESTINATION_KIND, destinationKind(request));
    boolean isTemporaryDestination = temporaryDestination(request);
    if (isTemporaryDestination) {
      set(attributes, SemanticAttributes.MESSAGING_TEMP_DESTINATION, true);
      set(attributes, SemanticAttributes.MESSAGING_DESTINATION, TEMP_DESTINATION_NAME);
    } else {
      set(attributes, SemanticAttributes.MESSAGING_DESTINATION, destination(request));
    }
    set(attributes, SemanticAttributes.MESSAGING_PROTOCOL, protocol(request));
    set(attributes, SemanticAttributes.MESSAGING_PROTOCOL_VERSION, protocolVersion(request));
    set(attributes, SemanticAttributes.MESSAGING_URL, url(request));
    set(attributes, SemanticAttributes.MESSAGING_CONVERSATION_ID, conversationId(request));
    set(
        attributes,
        SemanticAttributes.MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES,
        messagePayloadSize(request));
    set(
        attributes,
        SemanticAttributes.MESSAGING_MESSAGE_PAYLOAD_COMPRESSED_SIZE_BYTES,
        messagePayloadCompressedSize(request));
    MessageOperation operation = operation(request);
    if (operation == MessageOperation.RECEIVE || operation == MessageOperation.PROCESS) {
      set(attributes, SemanticAttributes.MESSAGING_OPERATION, operation.operationName());
    }
  }

  @Override
  protected final void onEnd(
      AttributesBuilder attributes, REQUEST request, @Nullable RESPONSE response) {
    set(attributes, SemanticAttributes.MESSAGING_MESSAGE_ID, messageId(request, response));
  }

  @Nullable
  protected abstract String system(REQUEST request);

  @Nullable
  protected abstract String destinationKind(REQUEST request);

  @Nullable
  protected abstract String destination(REQUEST request);

  protected abstract boolean temporaryDestination(REQUEST request);

  @Nullable
  protected abstract String protocol(REQUEST request);

  @Nullable
  protected abstract String protocolVersion(REQUEST request);

  @Nullable
  protected abstract String url(REQUEST request);

  @Nullable
  protected abstract String conversationId(REQUEST request);

  @Nullable
  protected abstract Long messagePayloadSize(REQUEST request);

  @Nullable
  protected abstract Long messagePayloadCompressedSize(REQUEST request);

  @Nullable
  protected abstract MessageOperation operation(REQUEST request);

  @Nullable
  protected abstract String messageId(REQUEST request, @Nullable RESPONSE response);
}
