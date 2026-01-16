"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.MessageTransformer = void 0;
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const protobuf_1 = require("@bufbuild/protobuf");
const varint = require("varint");
const utils_1 = require("./utils");
const embedded_sass_pb_1 = require("./vendor/embedded_sass_pb");
/**
 * Encodes InboundMessages into protocol buffers and decodes protocol buffers
 * into OutboundMessages.
 */
class MessageTransformer {
    outboundProtobufs$;
    writeInboundProtobuf;
    // The decoded messages are written to this Subject. It is publicly exposed
    // as a readonly Observable.
    outboundMessagesInternal$ = new rxjs_1.Subject();
    /**
     * The OutboundMessages, decoded from protocol buffers. If this fails to
     * decode a message, it will emit an error.
     */
    outboundMessages$ = this.outboundMessagesInternal$.pipe();
    constructor(outboundProtobufs$, writeInboundProtobuf) {
        this.outboundProtobufs$ = outboundProtobufs$;
        this.writeInboundProtobuf = writeInboundProtobuf;
        this.outboundProtobufs$
            .pipe((0, operators_1.map)(decode))
            .subscribe(this.outboundMessagesInternal$);
    }
    /**
     * Converts the inbound `compilationId` and `message` to a protocol buffer.
     */
    writeInboundMessage([compilationId, message]) {
        const compilationIdLength = varint.encodingLength(compilationId);
        const encodedMessage = (0, protobuf_1.toBinary)(embedded_sass_pb_1.InboundMessageSchema, message);
        const buffer = new Uint8Array(compilationIdLength + encodedMessage.length);
        varint.encode(compilationId, buffer);
        buffer.set(encodedMessage, compilationIdLength);
        try {
            this.writeInboundProtobuf(buffer);
        }
        catch (error) {
            this.outboundMessagesInternal$.error(error);
        }
    }
}
exports.MessageTransformer = MessageTransformer;
// Decodes a protobuf `buffer` into a compilation ID and an OutboundMessage,
// ensuring that all mandatory message fields are populated. Throws if `buffer`
// cannot be decoded into a valid message, or if the message itself contains a
// Protocol Error.
function decode(buffer) {
    let compilationId;
    try {
        compilationId = varint.decode(buffer);
    }
    catch (error) {
        throw (0, utils_1.compilerError)(`Invalid compilation ID varint: ${error}`);
    }
    try {
        return [
            compilationId,
            (0, protobuf_1.fromBinary)(embedded_sass_pb_1.OutboundMessageSchema, new Uint8Array(buffer.buffer, varint.decode.bytes)),
        ];
    }
    catch (error) {
        throw (0, utils_1.compilerError)(`Invalid protobuf: ${error}`);
    }
}
//# sourceMappingURL=message-transformer.js.map