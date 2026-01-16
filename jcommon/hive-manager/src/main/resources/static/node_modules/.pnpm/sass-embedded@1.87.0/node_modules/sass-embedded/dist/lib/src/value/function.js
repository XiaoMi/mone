"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassFunction = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
/** A first-class SassScript function. */
class SassFunction extends index_1.Value {
    /**
     * If this function is defined in the compiler, this is the unique ID that the
     * compiler uses to determine which function it refers to.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    id;
    /**
     * If this function is defined in the host, this is the signature that
     * describes how to pass arguments to it.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    signature;
    /**
     * If this function is defined in the host, this is the callback to run when
     * the function is invoked from a stylesheet.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    callback;
    constructor(idOrSignature, callback) {
        super();
        if (typeof idOrSignature === 'number') {
            this.id = idOrSignature;
        }
        else {
            this.signature = idOrSignature;
            this.callback = callback;
        }
    }
    equals(other) {
        return this.id === undefined
            ? other === this
            : other instanceof SassFunction && other.id === this.id;
    }
    hashCode() {
        return this.id === undefined ? (0, immutable_1.hash)(this.signature) : (0, immutable_1.hash)(this.id);
    }
    toString() {
        return this.signature ? this.signature : `<compiler function ${this.id}>`;
    }
}
exports.SassFunction = SassFunction;
//# sourceMappingURL=function.js.map