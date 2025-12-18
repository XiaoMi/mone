package run.mone.hive.shannon.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.shannon.exceptions.ControlProtocolError;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages request/response correlation for control protocol messages.
 * Ensures that responses are matched to the correct requests.
 */
public class CorrelationManager {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationManager.class);
    private static final long DEFAULT_TIMEOUT_MS = 30000; // 30 seconds

    private final AtomicLong requestIdCounter = new AtomicLong(0);
    private final Map<Long, PendingRequest> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Generate the next request ID.
     */
    public long nextRequestId() {
        return requestIdCounter.incrementAndGet();
    }

    /**
     * Register a pending request and return a future for the response.
     *
     * @param requestId the request ID
     * @param action the action being performed
     * @param timeoutMs timeout in milliseconds (0 for no timeout)
     * @return a future that will complete with the response
     */
    public CompletableFuture<ControlMessage> registerRequest(
        long requestId,
        String action,
        long timeoutMs
    ) {
        CompletableFuture<ControlMessage> future = new CompletableFuture<>();
        PendingRequest pending = new PendingRequest(action, future);

        pendingRequests.put(requestId, pending);
        logger.debug("Registered request {} for action: {}", requestId, action);

        // Set timeout if specified
        if (timeoutMs > 0) {
            future.orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        pendingRequests.remove(requestId);
                        logger.warn("Request {} timed out after {}ms", requestId, timeoutMs);
                    }
                });
        }

        return future;
    }

    /**
     * Register a pending request with default timeout.
     */
    public CompletableFuture<ControlMessage> registerRequest(long requestId, String action) {
        return registerRequest(requestId, action, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Handle a response and complete the corresponding request.
     *
     * @param response the response message
     * @return true if a pending request was found and completed
     */
    public boolean handleResponse(ControlMessage response) {
        if (response == null || !response.isResponse()) {
            logger.warn("Invalid response message: {}", response);
            return false;
        }

        Long requestId = response.getId();
        if (requestId == null) {
            logger.warn("Response missing request ID: {}", response);
            return false;
        }

        PendingRequest pending = pendingRequests.remove(requestId);
        if (pending == null) {
            logger.warn("No pending request found for ID: {}", requestId);
            return false;
        }

        logger.debug("Received response for request {}: status={}", requestId, response.getStatus());

        if (response.isError()) {
            pending.future.completeExceptionally(
                new ControlProtocolError(pending.action, response.getError())
            );
        } else {
            pending.future.complete(response);
        }

        return true;
    }

    /**
     * Cancel a pending request.
     *
     * @param requestId the request ID
     * @return true if a pending request was found and cancelled
     */
    public boolean cancel(long requestId) {
        PendingRequest pending = pendingRequests.remove(requestId);
        if (pending != null) {
            pending.future.cancel(true);
            logger.debug("Cancelled request {}", requestId);
            return true;
        }
        return false;
    }

    /**
     * Cancel all pending requests.
     */
    public void cancelAll() {
        logger.info("Cancelling {} pending requests", pendingRequests.size());
        pendingRequests.forEach((id, pending) -> {
            pending.future.cancel(true);
        });
        pendingRequests.clear();
    }

    /**
     * Get the number of pending requests.
     */
    public int getPendingCount() {
        return pendingRequests.size();
    }

    /**
     * Internal class to hold pending request information.
     */
    private static class PendingRequest {
        final String action;
        final CompletableFuture<ControlMessage> future;

        PendingRequest(String action, CompletableFuture<ControlMessage> future) {
            this.action = action;
            this.future = future;
        }
    }
}
