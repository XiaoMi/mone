"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyList = void 0;
const base_1 = require("./base");
const list_1 = require("../../value/list");
const null_1 = require("../../value/null");
const wrap_1 = require("./wrap");
class LegacyList extends base_1.LegacyValueBase {
    constructor(lengthOrInner, commaSeparator) {
        if (lengthOrInner instanceof list_1.SassList) {
            super(lengthOrInner);
            return;
        }
        super(new list_1.SassList(new Array(lengthOrInner).fill(null_1.sassNull), {
            separator: commaSeparator === false ? ' ' : ',',
        }));
    }
    getValue(index) {
        const length = this.inner.asList.size;
        if (index < 0 || index >= length) {
            throw new Error(`Invalid index ${index}, must be between 0 and ${length}`);
        }
        const value = this.inner.get(index);
        return value ? (0, wrap_1.wrapValue)(value) : undefined;
    }
    setValue(index, value) {
        this.inner = new list_1.SassList(this.inner.asList.set(index, (0, wrap_1.unwrapValue)(value)), {
            separator: this.inner.separator,
            brackets: this.inner.hasBrackets,
        });
    }
    getSeparator() {
        return this.inner.separator === ',';
    }
    setSeparator(isComma) {
        this.inner = new list_1.SassList(this.inner.asList, {
            separator: isComma ? ',' : ' ',
            brackets: this.inner.hasBrackets,
        });
    }
    getLength() {
        return this.inner.asList.size;
    }
}
exports.LegacyList = LegacyList;
Object.defineProperty(LegacyList, 'name', { value: 'sass.types.List' });
//# sourceMappingURL=list.js.map