"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassMixin = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
/** A first-class SassScript mixin. */
class SassMixin extends index_1.Value {
    /**
     * This is the unique ID that the compiler uses to determine which mixin it
     * refers to.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    id;
    constructor(id) {
        super();
        this.id = id;
    }
    equals(other) {
        return other instanceof SassMixin && other.id === this.id;
    }
    hashCode() {
        return (0, immutable_1.hash)(this.id);
    }
    toString() {
        return `<compiler mixin ${this.id}>`;
    }
    assertMixin() {
        return this;
    }
}
exports.SassMixin = SassMixin;
//# sourceMappingURL=mixin.js.map