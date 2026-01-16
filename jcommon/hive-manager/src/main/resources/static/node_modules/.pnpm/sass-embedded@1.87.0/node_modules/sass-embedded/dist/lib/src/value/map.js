"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassMap = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
const list_1 = require("./list");
/** A SassScript map */
class SassMap extends index_1.Value {
    contentsInternal;
    /** Returns a map that contains `contents`. */
    constructor(contents) {
        super();
        this.contentsInternal = contents ?? (0, immutable_1.OrderedMap)();
    }
    /** The separator for `this`'s contents as a list. */
    get separator() {
        return this.contentsInternal.isEmpty() ? null : ',';
    }
    /** `this`'s contents. */
    get contents() {
        return this.contentsInternal;
    }
    /**
     * Returns an immutable list of `contents`'s keys and values as two-element
     * `SassList`s.
     */
    get asList() {
        const list = [];
        for (const entry of this.contents.entries()) {
            list.push(new list_1.SassList(entry, { separator: ' ' }));
        }
        return (0, immutable_1.List)(list);
    }
    get lengthAsList() {
        return this.contentsInternal.size;
    }
    get(indexOrKey) {
        if (indexOrKey instanceof index_1.Value) {
            return this.contentsInternal.get(indexOrKey);
        }
        else {
            const entry = this.contentsInternal
                .entrySeq()
                .get(Math.floor(indexOrKey));
            return entry ? new list_1.SassList(entry, { separator: ' ' }) : undefined;
        }
    }
    assertMap() {
        return this;
    }
    tryMap() {
        return this;
    }
    equals(other) {
        if (other instanceof list_1.SassList &&
            this.contents.size === 0 &&
            other.asList.size === 0) {
            return true;
        }
        if (!(other instanceof SassMap) ||
            this.contents.size !== other.contents.size) {
            return false;
        }
        for (const [key, value] of this.contents.entries()) {
            const otherValue = other.contents.get(key);
            if (otherValue === undefined || !otherValue.equals(value)) {
                return false;
            }
        }
        return true;
    }
    hashCode() {
        return this.contents.isEmpty()
            ? new list_1.SassList().hashCode()
            : // SassMaps with the same key-value pairs are considered equal
                // regardless of key-value order, so this hash must be order
                // independent. Since OrderedMap.hashCode() encodes the key-value order,
                // we use a manual XOR accumulator instead.
                this.contents.reduce((accumulator, value, key) => accumulator ^ value.hashCode() ^ key.hashCode(), 0);
    }
    toString() {
        let string = '(';
        string += Array.from(this.contents.entries(), ([key, value]) => `${key}: ${value}`).join(', ');
        string += ')';
        return string;
    }
}
exports.SassMap = SassMap;
//# sourceMappingURL=map.js.map