"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.PacketTransformer = void 0;
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const BufferBuilder = require("buffer-builder");
/**
 * Decodes arbitrarily-chunked buffers, for example
 *   [ 0 1 2 3 4 5 6 7 ... ],
 * into packets of set length in the form
 *   +---------+------------- ...
 *   | 0 1 2 3 | 4 5 6 7 ...
 *   +---------+------------- ...
 *   | HEADER  | PAYLOAD (PROTOBUF)
 *   +---------+------------- ...
 * and emits the payload of each packet.
 *
 * Encodes packets by attaching a header to a protobuf that describes the
 * protobuf's length.
 */
class PacketTransformer {
    outboundBuffers$;
    writeInboundBuffer;
    // The packet that is actively being decoded as buffers come in.
    packet = new Packet();
    // The decoded protobufs are written to this Subject. It is publicly exposed
    // as a readonly Observable.
    outboundProtobufsInternal$ = new rxjs_1.Subject();
    /**
     * The fully-decoded, outbound protobufs. If any errors are encountered
     * during encoding/decoding, this Observable will error out.
     */
    outboundProtobufs$ = this.outboundProtobufsInternal$.pipe();
    constructor(outboundBuffers$, writeInboundBuffer) {
        this.outboundBuffers$ = outboundBuffers$;
        this.writeInboundBuffer = writeInboundBuffer;
        this.outboundBuffers$
            .pipe((0, operators_1.mergeMap)(buffer => this.decode(buffer)))
            .subscribe(this.outboundProtobufsInternal$);
    }
    /**
     * Encodes a packet by pre-fixing `protobuf` with a header that describes its
     * length.
     */
    writeInboundProtobuf(protobuf) {
        try {
            let length = protobuf.length;
            if (length === 0) {
                this.writeInboundBuffer(Buffer.alloc(1));
                return;
            }
            // Write the length in varint format, 7 bits at a time from least to most
            // significant.
            const header = new BufferBuilder(8);
            while (length > 0) {
                // The highest-order bit indicates whether more bytes are necessary to
                // fully express the number. The lower 7 bits indicate the number's
                // value.
                header.appendUInt8((length > 0x7f ? 0x80 : 0) | (length & 0x7f));
                length >>= 7;
            }
            const packet = Buffer.alloc(header.length + protobuf.length);
            header.copy(packet);
            packet.set(protobuf, header.length);
            this.writeInboundBuffer(packet);
        }
        catch (error) {
            this.outboundProtobufsInternal$.error(error);
        }
    }
    // Decodes a buffer, filling up the packet that is actively being decoded.
    // Returns a list of decoded payloads.
    decode(buffer) {
        const payloads = [];
        let decodedBytes = 0;
        while (decodedBytes < buffer.length) {
            decodedBytes += this.packet.write(buffer.slice(decodedBytes));
            if (this.packet.isComplete && this.packet.payload) {
                payloads.push(this.packet.payload);
                this.packet = new Packet();
            }
        }
        return payloads;
    }
}
exports.PacketTransformer = PacketTransformer;
/** A length-delimited packet comprised of a header and payload. */
class Packet {
    // The number of bits we've consumed so far to fill out `payloadLength`.
    payloadLengthBits = 0;
    // The length of the next message, in bytes.
    //
    // This is built up from a [varint]. Once it's fully consumed, `payload` is
    // initialized and this is reset to 0.
    //
    // [varint]: https://developers.google.com/protocol-buffers/docs/encoding#varints
    payloadLength = 0;
    /**
     * The packet's payload. Constructed by calls to write().
     * @see write
     */
    payload;
    // The offset in [payload] that should be written to next time data arrives.
    payloadOffset = 0;
    /** Whether the packet construction is complete. */
    get isComplete() {
        return !!(this.payload && this.payloadOffset >= this.payloadLength);
    }
    /**
     * Takes arbitrary binary input and slots it into the header and payload
     * appropriately. Returns the number of bytes that were written into the
     * packet. This method can be called repeatedly, incrementally building
     * up the packet until it is complete.
     */
    write(source) {
        if (this.isComplete) {
            throw Error('Cannot write to a completed Packet.');
        }
        // The index of the next byte to read from [source]. We have to track this
        // because the source may contain the length *and* the message.
        let i = 0;
        // We can be in one of two states here:
        //
        // * [payload] is `null`, in which case we're adding data to [payloadLength]
        //   until we reach a byte with its most significant bit set to 0.
        //
        // * [payload] is not `null`, in which case we're waiting for
        //   [payloadOffset] to reach [payloadLength] bytes in it so this packet is
        //   complete.
        if (!this.payload) {
            for (;;) {
                const byte = source[i];
                // Varints encode data in the 7 lower bits of each byte, which we access
                // by masking with 0x7f = 0b01111111.
                this.payloadLength += (byte & 0x7f) << this.payloadLengthBits;
                this.payloadLengthBits += 7;
                i++;
                if (byte <= 0x7f) {
                    // If the byte is lower than 0x7f = 0b01111111, that means its high
                    // bit is unset which and we now know the full message length and can
                    // initialize [this.payload].
                    this.payload = Buffer.alloc(this.payloadLength);
                    break;
                }
                else if (i === source.length) {
                    // If we've hit the end of the source chunk, we need to wait for the
                    // next chunk to arrive. Just return the number of bytes we've
                    // consumed so far.
                    return i;
                }
                else {
                    // Otherwise, we continue reading bytes from the source data to fill
                    // in [this.payloadLength].
                }
            }
        }
        // Copy as many bytes as we can from [source] to [payload], making sure not
        // to try to copy more than the payload can hold (if the source has another
        // message after the current one) or more than the source has available (if
        // the current message is split across multiple chunks).
        const bytesToWrite = Math.min(this.payload.length - this.payloadOffset, source.length - i);
        this.payload.set(source.subarray(i, i + bytesToWrite), this.payloadOffset);
        this.payloadOffset += bytesToWrite;
        return i + bytesToWrite;
    }
}
//# sourceMappingURL=packet-transformer.js.map