"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.sassFalse = exports.sassTrue = exports.SassBooleanInternal = exports.SassBoolean = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
/**
 * Sass boolean.
 *
 * This is an abstract class that cannot be directly instantiated. Instead,
 * use the provided {@link sassTrue} and {@link sassFalse} singleton instances.
 */
class SassBoolean extends index_1.Value {
}
exports.SassBoolean = SassBoolean;
const trueHash = (0, immutable_1.hash)(true);
const falseHash = (0, immutable_1.hash)(false);
class SassBooleanInternal extends SassBoolean {
    valueInternal;
    // Whether callers are allowed to construct this class. This is set to
    // `false` once the two constants are constructed so that the constructor
    // throws an error for future calls, in accordance with the legacy API.
    static constructionAllowed = true;
    constructor(valueInternal) {
        super();
        this.valueInternal = valueInternal;
        if (!SassBooleanInternal.constructionAllowed) {
            throw ("new sass.types.Boolean() isn't allowed.\n" +
                'Use sass.types.Boolean.TRUE or sass.types.Boolean.FALSE instead.');
        }
        Object.freeze(this);
    }
    get value() {
        return this.valueInternal;
    }
    get isTruthy() {
        return this.value;
    }
    assertBoolean() {
        return this;
    }
    equals(other) {
        return this === other;
    }
    hashCode() {
        return this.value ? trueHash : falseHash;
    }
    toString() {
        return this.value ? 'sassTrue' : 'sassFalse';
    }
    // Legacy API support
    static TRUE;
    static FALSE;
    getValue() {
        return this.value;
    }
}
exports.SassBooleanInternal = SassBooleanInternal;
/** The singleton instance of SassScript true. */
exports.sassTrue = new SassBooleanInternal(true);
/** The singleton instance of SassScript false. */
exports.sassFalse = new SassBooleanInternal(false);
// Legacy API support
SassBooleanInternal.constructionAllowed = false;
SassBooleanInternal.TRUE = exports.sassTrue;
SassBooleanInternal.FALSE = exports.sassFalse;
//# sourceMappingURL=boolean.js.map