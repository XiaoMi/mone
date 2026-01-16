"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.sassNull = exports.SassNull = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
const hashCode = (0, immutable_1.hash)(null);
// SassScript null. Cannot be constructed; exists only as the exported
// singleton.
class SassNull extends index_1.Value {
    // Whether callers are allowed to construct this class. This is set to
    // `false` once the two constants are constructed so that the constructor
    // throws an error for future calls, in accordance with the legacy API.
    static constructionAllowed = true;
    constructor() {
        super();
        if (!SassNull.constructionAllowed) {
            throw ("new sass.types.Null() isn't allowed.\n" +
                'Use sass.types.Null.NULL instead.');
        }
        Object.freeze(this);
    }
    get isTruthy() {
        return false;
    }
    get realNull() {
        return null;
    }
    equals(other) {
        return this === other;
    }
    hashCode() {
        return hashCode;
    }
    toString() {
        return 'sassNull';
    }
    // Legacy API support
    static NULL;
}
exports.SassNull = SassNull;
/** The singleton instance of SassScript null. */
exports.sassNull = new SassNull();
// Legacy API support
SassNull.constructionAllowed = false;
SassNull.NULL = exports.sassNull;
//# sourceMappingURL=null.js.map