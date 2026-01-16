"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SyncMessagePort = exports.TimeoutException = void 0;
const assert_1 = require("assert");
const events_1 = require("events");
const worker_threads_1 = require("worker_threads");
/**
 * An enum of possible states for the shared buffer that two `SyncMessagePort`s
 * use to communicate.
 */
var BufferState;
(function (BufferState) {
    /**
     * The initial state. When an endpoint is ready to receive messages, it'll set
     * the buffer to this state so that it can use `Atomics.wait()` to be notified
     * when it switches to `MessageSent`.
     */
    BufferState[BufferState["AwaitingMessage"] = 0] = "AwaitingMessage";
    /**
     * The state indicating that a message has been sent. Whenever an endpoint
     * sends a message, it'll set the buffer to this state so that the other
     * endpoint's `Atomics.wait()` call terminates.
     */
    BufferState[BufferState["MessageSent"] = 1] = "MessageSent";
    /**
     * The bitmask indicating that the channel has been closed. This is masked on
     * top of AwaitingMessage and MessageSent state. It never transitions to any
     * other states once closed.
     */
    BufferState[BufferState["Closed"] = 2] = "Closed";
})(BufferState || (BufferState = {}));
/**
 * An exception thrown by {@link SyncMessagePort.receiveMessage} if a message
 * isn't received within {@link ReceivedMessageOptions.timeout} milliseconds.
 */
class TimeoutException extends Error {
    constructor(message) {
        super(message);
    }
}
exports.TimeoutException = TimeoutException;
/**
 * A communication port that can receive messages synchronously from another
 * `SyncMessagePort`.
 *
 * This also emits the same asynchronous events as `MessagePort`.
 */
class SyncMessagePort extends events_1.EventEmitter {
    port;
    /** Creates a channel whose ports can be passed to `new SyncMessagePort()`. */
    static createChannel() {
        const channel = new worker_threads_1.MessageChannel();
        // Four bytes is the minimum necessary to use `Atomics.wait()`.
        const buffer = new SharedArrayBuffer(4);
        // Queue up messages on each port so the caller doesn't have to explicitly
        // pass the buffer around along with them.
        channel.port1.postMessage(buffer);
        channel.port2.postMessage(buffer);
        return channel;
    }
    /**
     * An Int32 view of the shared buffer.
     *
     * Each port sets this to `BufferState.AwaitingMessage` before checking for
     * new messages in `receiveMessage()`, and each port sets it to
     * `BufferState.MessageSent` after sending a new message. It's set to
     * `BufferState.Closed` when the channel is closed.
     */
    buffer;
    /**
     * Creates a new message port. The `port` must be created by
     * `SyncMessagePort.createChannel()` and must connect to a port passed to
     * another `SyncMessagePort` in another worker.
     */
    constructor(port) {
        super();
        this.port = port;
        const buffer = (0, worker_threads_1.receiveMessageOnPort)(this.port)?.message;
        if (!buffer) {
            throw new Error('new SyncMessagePort() must be passed a port from ' +
                'SyncMessagePort.createChannel().');
        }
        this.buffer = new Int32Array(buffer);
        this.on('newListener', (event, listener) => {
            this.port.on(event, listener);
        });
        this.on('removeListener', (event, listener) => this.port.removeListener(event, listener));
    }
    /** See `MessagePort.postMesage()`. */
    postMessage(value, transferList) {
        this.port.postMessage(value, transferList);
        // If the other port is waiting for a new message, notify it that the
        // message is ready. Use `Atomics.compareExchange` so that we don't
        // overwrite the "closed" state.
        if (Atomics.compareExchange(this.buffer, 0, BufferState.AwaitingMessage, BufferState.MessageSent) === BufferState.AwaitingMessage) {
            Atomics.notify(this.buffer, 0);
        }
    }
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
    receiveMessageIfAvailable() {
        if (this.listenerCount('message')) {
            throw new Error('SyncMessageChannel.receiveMessageIfAvailable() may not be called ' +
                'while there are message listeners.');
        }
        return (0, worker_threads_1.receiveMessageOnPort)(this.port);
    }
    /**
     * Blocks and returns the next message sent by the other port.
     *
     * This may not be called while this has a listener for the `'message'` event.
     * Throws an error if the channel is closed, including if it closes while this
     * is waiting for a message, unless {@link ReceiveMessageOptions.closedValue}
     * is passed.
     */
    receiveMessage(options) {
        if (this.listenerCount('message')) {
            throw new Error('SyncMessageChannel.receiveMessage() may not be called while there ' +
                'are message listeners.');
        }
        // Set the "new message" indicator to zero before we check for new messages.
        // That way if the other port sets it to 1 between the call to
        // `receiveMessageOnPort` and the call to `Atomics.wait()`, we won't
        // overwrite it. Use `Atomics.compareExchange` so that we don't overwrite
        // the "closed" state.
        const previousState = Atomics.compareExchange(this.buffer, 0, BufferState.MessageSent, BufferState.AwaitingMessage);
        if (previousState === BufferState.Closed) {
            if (options && 'closedValue' in options)
                return options.closedValue;
            throw new Error("The SyncMessagePort's channel is closed.");
        }
        let message = (0, worker_threads_1.receiveMessageOnPort)(this.port);
        if (message)
            return message.message;
        // If there's no new message, wait for the other port to flip the "new
        // message" indicator to 1. If it's been set to 1 since we stored 0, this
        // will terminate immediately.
        const result = Atomics.wait(this.buffer, 0, BufferState.AwaitingMessage, options?.timeout);
        message = (0, worker_threads_1.receiveMessageOnPort)(this.port);
        if (message)
            return message.message;
        if (result === 'timed-out') {
            if ('timeoutValue' in options)
                return options.timeoutValue;
            throw new TimeoutException('SyncMessagePort.receiveMessage() timed out.');
        }
        // Update the state to 0b10 after the last message is consumed.
        const oldState = Atomics.and(this.buffer, 0, BufferState.Closed);
        // Assert the old state was either 0b10 or 0b11.
        assert_1.strict.equal(oldState & BufferState.Closed, BufferState.Closed);
        if (options && 'closedValue' in options)
            return options.closedValue;
        throw new Error("The SyncMessagePort's channel is closed.");
    }
    /** See `MessagePort.close()`. */
    close() {
        Atomics.or(this.buffer, 0, BufferState.Closed);
        Atomics.notify(this.buffer, 0);
        this.port.close();
    }
}
exports.SyncMessagePort = SyncMessagePort;
//# sourceMappingURL=index.js.map