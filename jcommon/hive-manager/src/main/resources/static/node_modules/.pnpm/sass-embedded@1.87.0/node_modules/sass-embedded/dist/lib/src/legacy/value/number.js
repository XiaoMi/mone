"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyNumber = void 0;
const number_1 = require("../../value/number");
const base_1 = require("./base");
class LegacyNumber extends base_1.LegacyValueBase {
    constructor(valueOrInner, unit) {
        super(valueOrInner instanceof number_1.SassNumber
            ? valueOrInner
            : parseNumber(valueOrInner, unit));
    }
    getValue() {
        return this.inner.value;
    }
    setValue(value) {
        this.inner = new number_1.SassNumber(value, {
            numeratorUnits: this.inner.numeratorUnits,
            denominatorUnits: this.inner.denominatorUnits,
        });
    }
    getUnit() {
        return (this.inner.numeratorUnits.join('*') +
            (this.inner.denominatorUnits.size === 0 ? '' : '/') +
            this.inner.denominatorUnits.join('*'));
    }
    setUnit(unit) {
        this.inner = parseNumber(this.inner.value, unit);
    }
}
exports.LegacyNumber = LegacyNumber;
Object.defineProperty(LegacyNumber, 'name', { value: 'sass.types.Number' });
// Parses a `SassNumber` from `value` and `unit`, using Node Sass's unit
// format.
function parseNumber(value, unit) {
    if (!unit)
        return new number_1.SassNumber(value);
    if (!unit.includes('*') && !unit.includes('/')) {
        return new number_1.SassNumber(value, unit);
    }
    const invalidUnit = new Error(`Unit ${unit} is invalid`);
    const operands = unit.split('/');
    if (operands.length > 2)
        throw invalidUnit;
    const numerator = operands[0];
    const denominator = operands.length === 1 ? null : operands[1];
    const numeratorUnits = numerator.length === 0 ? [] : numerator.split('*');
    if (numeratorUnits.some(unit => unit.length === 0))
        throw invalidUnit;
    const denominatorUnits = denominator === null ? [] : denominator.split('*');
    if (denominatorUnits.some(unit => unit.length === 0))
        throw invalidUnit;
    return new number_1.SassNumber(value, {
        numeratorUnits: numeratorUnits,
        denominatorUnits: denominatorUnits,
    });
}
//# sourceMappingURL=number.js.map