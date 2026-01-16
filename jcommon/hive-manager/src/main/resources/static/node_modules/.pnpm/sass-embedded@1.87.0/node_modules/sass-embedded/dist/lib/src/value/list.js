"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassList = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
const map_1 = require("./map");
const utils_1 = require("../utils");
// All empty SassList and SassMaps should have the same hashcode, so this caches
// the value.
const emptyListHashCode = (0, immutable_1.hash)([]);
/** A SassScript list. */
class SassList extends index_1.Value {
    contentsInternal;
    separatorInternal;
    hasBracketsInternal;
    constructor(contentsOrOptions, options) {
        super();
        if ((0, immutable_1.isList)(contentsOrOptions) || Array.isArray(contentsOrOptions)) {
            this.contentsInternal = (0, utils_1.asImmutableList)(contentsOrOptions);
        }
        else {
            this.contentsInternal = (0, immutable_1.List)();
            options = contentsOrOptions;
        }
        if (this.contentsInternal.size > 1 && options?.separator === null) {
            throw Error('Non-null separator required for SassList with more than one element.');
        }
        this.separatorInternal =
            options?.separator === undefined ? ',' : options.separator;
        this.hasBracketsInternal = options?.brackets ?? false;
    }
    get asList() {
        return this.contentsInternal;
    }
    /** Whether `this` has brackets. */
    get hasBrackets() {
        return this.hasBracketsInternal;
    }
    /** `this`'s list separator. */
    get separator() {
        return this.separatorInternal;
    }
    get lengthAsList() {
        return this.contentsInternal.size;
    }
    get(index) {
        return this.contentsInternal.get(index);
    }
    assertList() {
        return this;
    }
    assertMap(name) {
        if (this.contentsInternal.isEmpty())
            return new map_1.SassMap();
        throw (0, utils_1.valueError)(`${this} is not a map`, name);
    }
    /**
     * If `this` is empty, returns an empty OrderedMap.
     *
     * Otherwise, returns null.
     */
    tryMap() {
        return this.contentsInternal.isEmpty() ? new map_1.SassMap() : null;
    }
    equals(other) {
        if ((other instanceof SassList || other instanceof map_1.SassMap) &&
            this.contentsInternal.isEmpty() &&
            other.asList.isEmpty()) {
            return true;
        }
        if (!(other instanceof SassList) ||
            this.hasBrackets !== other.hasBrackets ||
            this.separator !== other.separator) {
            return false;
        }
        return this.contentsInternal.equals(other.asList);
    }
    hashCode() {
        return this.contentsInternal.isEmpty()
            ? emptyListHashCode
            : this.contentsInternal.hashCode() ^
                (0, immutable_1.hash)(this.hasBrackets) ^
                (0, immutable_1.hash)(this.separator);
    }
    toString() {
        let string = '';
        if (this.hasBrackets)
            string += '[';
        string += `${this.contentsInternal.join(this.separator === ' ' || this.separator === null
            ? ' '
            : `${this.separator} `)}`;
        if (this.hasBrackets)
            string += ']';
        return string;
    }
}
exports.SassList = SassList;
//# sourceMappingURL=list.js.map