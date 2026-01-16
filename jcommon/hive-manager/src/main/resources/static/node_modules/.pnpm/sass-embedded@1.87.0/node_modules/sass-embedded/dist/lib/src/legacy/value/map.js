"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyMap = void 0;
const immutable_1 = require("immutable");
const base_1 = require("./base");
const map_1 = require("../../value/map");
const number_1 = require("../../value/number");
const null_1 = require("../../value/null");
const wrap_1 = require("./wrap");
class LegacyMap extends base_1.LegacyValueBase {
    constructor(lengthOrInner) {
        if (lengthOrInner instanceof map_1.SassMap) {
            super(lengthOrInner);
            return;
        }
        super(new map_1.SassMap((0, immutable_1.OrderedMap)(Array.from({ length: lengthOrInner }, (_, i) => [
            new number_1.SassNumber(i),
            null_1.sassNull,
        ]))));
    }
    getValue(index) {
        const value = this.inner.contents.valueSeq().get(index);
        if (index < 0 || !value) {
            throw new Error(`Invalid index ${index}, must be between 0 and ` +
                this.inner.contents.size);
        }
        return (0, wrap_1.wrapValue)(value);
    }
    setValue(index, value) {
        this.inner = new map_1.SassMap(this.inner.contents.set(this.getUnwrappedKey(index), (0, wrap_1.unwrapValue)(value)));
    }
    getKey(index) {
        return (0, wrap_1.wrapValue)(this.getUnwrappedKey(index));
    }
    // Like `getKey()`, but returns the unwrapped non-legacy value.
    getUnwrappedKey(index) {
        const key = this.inner.contents.keySeq().get(index);
        if (index >= 0 && key)
            return key;
        throw new Error(`Invalid index ${index}, must be between 0 and ` +
            this.inner.contents.size);
    }
    setKey(index, key) {
        const oldMap = this.inner.contents;
        if (index < 0 || index >= oldMap.size) {
            throw new Error(`Invalid index ${index}, must be between 0 and ${oldMap.size}`);
        }
        const newKey = (0, wrap_1.unwrapValue)(key);
        const newMap = (0, immutable_1.OrderedMap)().asMutable();
        let i = 0;
        for (const [oldKey, oldValue] of oldMap.entries()) {
            if (i === index) {
                newMap.set(newKey, oldValue);
            }
            else {
                if (newKey.equals(oldKey)) {
                    throw new Error(`${key} is already in the map`);
                }
                newMap.set(oldKey, oldValue);
            }
            i++;
        }
        this.inner = new map_1.SassMap(newMap.asImmutable());
    }
    getLength() {
        return this.inner.contents.size;
    }
}
exports.LegacyMap = LegacyMap;
Object.defineProperty(LegacyMap, 'name', { value: 'sass.types.Map' });
//# sourceMappingURL=map.js.map