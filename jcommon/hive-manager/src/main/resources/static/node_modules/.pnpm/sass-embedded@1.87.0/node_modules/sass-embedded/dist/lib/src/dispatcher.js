"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.Dispatcher = void 0;
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const protobuf_1 = require("@bufbuild/protobuf");
const proto = require("./vendor/embedded_sass_pb");
const request_tracker_1 = require("./request-tracker");
const utils_1 = require("./utils");
/**
 * Dispatches requests, responses, and events for a single compilation.
 *
 * Accepts callbacks for processing different types of outbound requests. When
 * an outbound request arrives, this runs the appropriate callback to process
 * it, and then sends the result inbound. A single callback must be provided for
 * each outbound request type. The callback does not need to set the response
 * ID; the dispatcher handles it.
 *
 * Consumers can send an inbound request. This returns a promise that will
 * either resolve with the corresponding outbound response, or error if any
 * Protocol Errors were encountered. The consumer does not need to set the
 * request ID; the dispatcher handles it.
 *
 * Outbound events are exposed as Observables.
 *
 * Errors are not otherwise exposed to the top-level. Instead, they are surfaced
 * as an Observable that consumers may choose to subscribe to. Subscribers must
 * perform proper error handling.
 */
class Dispatcher {
    compilationId;
    outboundMessages$;
    writeInboundMessage;
    outboundRequestHandlers;
    // Tracks the IDs of all outbound requests. An inbound response with matching
    // ID and type will remove the ID.
    pendingOutboundRequests = new request_tracker_1.RequestTracker();
    // All outbound messages for this compilation. If we detect any errors while
    // dispatching messages, this completes.
    messages$ = new rxjs_1.Subject();
    // Subject to unsubscribe from all outbound messages to prevent past
    // dispatchers with compilation IDs reused by future dispatchers from
    // receiving messages intended for future dispatchers.
    unsubscribe$ = new rxjs_1.Subject();
    // If the dispatcher encounters an error, this errors out. It is publicly
    // exposed as a readonly Observable.
    errorInternal$ = new rxjs_1.Subject();
    /**
     * If the dispatcher encounters an error, this errors out. Upon error, the
     * dispatcher rejects all promises awaiting an outbound response, and silently
     * closes all subscriptions to outbound events.
     */
    error$ = this.errorInternal$.pipe();
    /**
     * Outbound log events. If an error occurs, the dispatcher closes this
     * silently.
     */
    logEvents$ = this.messages$.pipe((0, operators_1.filter)(message => message.message.case === 'logEvent'), (0, operators_1.map)(message => message.message.value));
    constructor(compilationId, outboundMessages$, writeInboundMessage, outboundRequestHandlers) {
        this.compilationId = compilationId;
        this.outboundMessages$ = outboundMessages$;
        this.writeInboundMessage = writeInboundMessage;
        this.outboundRequestHandlers = outboundRequestHandlers;
        if (compilationId < 1) {
            throw Error(`Invalid compilation ID ${compilationId}.`);
        }
        this.outboundMessages$
            .pipe((0, operators_1.filter)(([compilationId]) => compilationId === this.compilationId), (0, operators_1.map)(([, message]) => message), (0, operators_1.mergeMap)(message => {
            const result = this.handleOutboundMessage(message);
            return result instanceof Promise
                ? result.then(() => message)
                : [message];
        }), (0, operators_1.takeUntil)(this.unsubscribe$))
            .subscribe({
            next: message => this.messages$.next(message),
            error: error => this.throwAndClose(error),
            complete: () => {
                this.messages$.complete();
                this.errorInternal$.complete();
            },
        });
    }
    /**
     * Sends a CompileRequest inbound. Passes the corresponding outbound
     * CompileResponse or an error to `callback` and unsubscribes from all
     * outbound events.
     *
     * This uses an old-style callback argument so that it can work either
     * synchronously or asynchronously. If the underlying stdout stream emits
     * events synchronously, `callback` will be called synchronously.
     */
    sendCompileRequest(request, callback) {
        // Call the callback but unsubscribe first
        const callback_ = (err, response) => {
            this.unsubscribe();
            return callback(err, response);
        };
        if (this.messages$.isStopped) {
            callback_(new Error('Tried writing to closed dispatcher'), undefined);
            return;
        }
        this.messages$
            .pipe((0, operators_1.filter)(message => message.message.case === 'compileResponse'), (0, operators_1.map)(message => message.message.value))
            .subscribe({ next: response => callback_(null, response) });
        this.error$.subscribe({
            error: error => callback_(error, undefined),
        });
        try {
            this.writeInboundMessage([
                this.compilationId,
                (0, protobuf_1.create)(proto.InboundMessageSchema, {
                    message: { value: request, case: 'compileRequest' },
                }),
            ]);
        }
        catch (error) {
            this.throwAndClose(error);
        }
    }
    // Stop the outbound message subscription.
    unsubscribe() {
        this.unsubscribe$.next(undefined);
        this.unsubscribe$.complete();
    }
    // Rejects with `error` all promises awaiting an outbound response, and
    // silently closes all subscriptions awaiting outbound events.
    throwAndClose(error) {
        this.messages$.complete();
        this.errorInternal$.error(error);
        this.unsubscribe();
    }
    // Keeps track of all outbound messages. If the outbound `message` contains a
    // request or response, registers it with pendingOutboundRequests. If it
    // contains a request, runs the appropriate callback to generate an inbound
    // response, and then sends it inbound.
    handleOutboundMessage(message) {
        switch (message.message.case) {
            case 'logEvent':
                // Handled separately by `logEvents$`.
                return undefined;
            case 'compileResponse':
                // Handled separately by `sendCompileRequest`.
                return undefined;
            case 'importRequest': {
                const request = message.message.value;
                const id = request.id;
                const type = 'importResponse';
                this.pendingOutboundRequests.add(id, type);
                return (0, utils_1.thenOr)(this.outboundRequestHandlers.handleImportRequest(request), response => {
                    this.sendInboundMessage(id, { case: type, value: response });
                });
            }
            case 'fileImportRequest': {
                const request = message.message.value;
                const id = request.id;
                const type = 'fileImportResponse';
                this.pendingOutboundRequests.add(id, type);
                return (0, utils_1.thenOr)(this.outboundRequestHandlers.handleFileImportRequest(request), response => {
                    this.sendInboundMessage(id, { case: type, value: response });
                });
            }
            case 'canonicalizeRequest': {
                const request = message.message.value;
                const id = request.id;
                const type = 'canonicalizeResponse';
                this.pendingOutboundRequests.add(id, type);
                return (0, utils_1.thenOr)(this.outboundRequestHandlers.handleCanonicalizeRequest(request), response => {
                    this.sendInboundMessage(id, { case: type, value: response });
                });
            }
            case 'functionCallRequest': {
                const request = message.message.value;
                const id = request.id;
                const type = 'functionCallResponse';
                this.pendingOutboundRequests.add(id, type);
                return (0, utils_1.thenOr)(this.outboundRequestHandlers.handleFunctionCallRequest(request), response => {
                    this.sendInboundMessage(id, { case: type, value: response });
                });
            }
            case 'error':
                throw (0, utils_1.hostError)(message.message.value.message);
            default:
                throw (0, utils_1.compilerError)(`Unknown message type ${message.message.case}`);
        }
    }
    // Sends a message inbound. Keeps track of all pending inbound requests.
    sendInboundMessage(requestId, message) {
        message.value.id = requestId;
        if (message.case === 'importResponse' ||
            message.case === 'fileImportResponse' ||
            message.case === 'canonicalizeResponse' ||
            message.case === 'functionCallResponse') {
            this.pendingOutboundRequests.resolve(requestId, message.case);
        }
        else {
            throw Error(`Unknown message type ${message.case}`);
        }
        this.writeInboundMessage([
            this.compilationId,
            (0, protobuf_1.create)(proto.InboundMessageSchema, { message }),
        ]);
    }
}
exports.Dispatcher = Dispatcher;
//# sourceMappingURL=dispatcher.js.map