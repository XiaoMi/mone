import { EventEmitter } from 'events';
import { MessageChannel, MessagePort, TransferListItem } from 'worker_threads';
/**
 * Options that can be passed to {@link SyncMessagePort.receiveMessage}.
 */
export interface ReceiveMessageOptions {
    /**
     * The time (in milliseconds) to wait for a message before returning {@link
     * timeoutValue} (if set) or throwing a [TimeoutException] otherwise.
     */
    timeout?: number;
    /**
     * If a message isn't received within {@link timeout} milliseconds, this value
     * is returned. Ignored if {@link timeout} is not set.
     */
    timeoutValue?: unknown;
    /**
     * If the underlying channel is closed before calling {@link
     * SyncMessagePort.receiveMessage} or while a call is pending, return this
     * value.
     */
    closedValue?: unknown;
}
/**
 * An exception thrown by {@link SyncMessagePort.receiveMessage} if a message
 * isn't received within {@link ReceivedMessageOptions.timeout} milliseconds.
 */
export declare class TimeoutException extends Error {
    constructor(message: string);
}
/**
 * A communication port that can receive messages synchronously from another
 * `SyncMessagePort`.
 *
 * This also emits the same asynchronous events as `MessagePort`.
 */
export declare class SyncMessagePort extends EventEmitter {
    private readonly port;
    /** Creates a channel whose ports can be passed to `new SyncMessagePort()`. */
    static createChannel(): MessageChannel;
    /**
     * An Int32 view of the shared buffer.
     *
     * Each port sets this to `BufferState.AwaitingMessage` before checking for
     * new messages in `receiveMessage()`, and each port sets it to
     * `BufferState.MessageSent` after sending a new message. It's set to
     * `BufferState.Closed` when the channel is closed.
     */
    private readonly buffer;
    /**
     * Creates a new message port. The `port` must be created by
     * `SyncMessagePort.createChannel()` and must connect to a port passed to
     * another `SyncMessagePort` in another worker.
     */
    constructor(port: MessagePort);
    /** See `MessagePort.postMesage()`. */
    postMessage(value: unknown, transferList?: TransferListItem[]): void;
    /**
     * Returns the message sent by the other port, if one is available. This *does
     * not* block, and will return `undefined` immediately if no message is
     * available. In order to distinguish between a message with value `undefined`
     * and no message, a message is return in an object with a `message` field.
     *
     * This may not be called while this has a listener for the `'message'` event.
     * It does *not* throw an error if the port is closed when this is called;
     * instead, it just returns `undefined`.
     */
    receiveMessageIfAvailable(): {
        message: unknown;
    } | undefined;
    /**
     * Blocks and returns the next message sent by the other port.
     *
     * This may not be called while this has a listener for the `'message'` event.
     * Throws an error if the channel is closed, including if it closes while this
     * is waiting for a message, unless {@link ReceiveMessageOptions.closedValue}
     * is passed.
     */
    receiveMessage(options?: ReceiveMessageOptions): unknown;
    /** See `MessagePort.close()`. */
    close(): void;
}
