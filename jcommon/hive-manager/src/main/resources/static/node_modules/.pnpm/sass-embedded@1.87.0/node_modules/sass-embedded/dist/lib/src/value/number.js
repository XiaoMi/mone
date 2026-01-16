"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassNumber = void 0;
const immutable_1 = require("immutable");
const utils_1 = require("../utils");
const index_1 = require("./index");
const utils_2 = require("./utils");
// Conversion rates for each unit.
const conversions = {
    // Length
    in: {
        in: 1,
        cm: 1 / 2.54,
        pc: 1 / 6,
        mm: 1 / 25.4,
        q: 1 / 101.6,
        pt: 1 / 72,
        px: 1 / 96,
    },
    cm: {
        in: 2.54,
        cm: 1,
        pc: 2.54 / 6,
        mm: 1 / 10,
        q: 1 / 40,
        pt: 2.54 / 72,
        px: 2.54 / 96,
    },
    pc: {
        in: 6,
        cm: 6 / 2.54,
        pc: 1,
        mm: 6 / 25.4,
        q: 6 / 101.6,
        pt: 1 / 12,
        px: 1 / 16,
    },
    mm: {
        in: 25.4,
        cm: 10,
        pc: 25.4 / 6,
        mm: 1,
        q: 1 / 4,
        pt: 25.4 / 72,
        px: 25.4 / 96,
    },
    q: {
        in: 101.6,
        cm: 40,
        pc: 101.6 / 6,
        mm: 4,
        q: 1,
        pt: 101.6 / 72,
        px: 101.6 / 96,
    },
    pt: {
        in: 72,
        cm: 72 / 2.54,
        pc: 12,
        mm: 72 / 25.4,
        q: 72 / 101.6,
        pt: 1,
        px: 3 / 4,
    },
    px: {
        in: 96,
        cm: 96 / 2.54,
        pc: 16,
        mm: 96 / 25.4,
        q: 96 / 101.6,
        pt: 4 / 3,
        px: 1,
    },
    // Rotation
    deg: {
        deg: 1,
        grad: 9 / 10,
        rad: 180 / Math.PI,
        turn: 360,
    },
    grad: {
        deg: 10 / 9,
        grad: 1,
        rad: 200 / Math.PI,
        turn: 400,
    },
    rad: {
        deg: Math.PI / 180,
        grad: Math.PI / 200,
        rad: 1,
        turn: 2 * Math.PI,
    },
    turn: {
        deg: 1 / 360,
        grad: 1 / 400,
        rad: 1 / (2 * Math.PI),
        turn: 1,
    },
    // Time
    s: {
        s: 1,
        ms: 1 / 1000,
    },
    ms: {
        s: 1000,
        ms: 1,
    },
    // Frequency
    Hz: { Hz: 1, kHz: 1000 },
    kHz: { Hz: 1 / 1000, kHz: 1 },
    // Pixel density
    dpi: {
        dpi: 1,
        dpcm: 2.54,
        dppx: 96,
    },
    dpcm: {
        dpi: 1 / 2.54,
        dpcm: 1,
        dppx: 96 / 2.54,
    },
    dppx: {
        dpi: 1 / 96,
        dpcm: 2.54 / 96,
        dppx: 1,
    },
};
// A map from each human-readable type of unit to the units that belong to that
// type.
const unitsByType = {
    length: ['in', 'cm', 'pc', 'mm', 'q', 'pt', 'px'],
    angle: ['deg', 'grad', 'rad', 'turn'],
    time: ['s', 'ms'],
    frequency: ['Hz', 'kHz'],
    'pixel density': ['dpi', 'dpcm', 'dppx'],
};
// A map from each unit to its human-readable type.
const typesByUnit = {};
for (const [type, units] of Object.entries(unitsByType)) {
    for (const unit of units) {
        typesByUnit[unit] = type;
    }
}
/** A SassScript number. */
class SassNumber extends index_1.Value {
    valueInternal;
    numeratorUnitsInternal;
    denominatorUnitsInternal;
    constructor(value, unitOrOptions) {
        super();
        if (typeof unitOrOptions === 'string') {
            this.valueInternal = value;
            this.numeratorUnitsInternal =
                unitOrOptions === undefined ? (0, immutable_1.List)([]) : (0, immutable_1.List)([unitOrOptions]);
            this.denominatorUnitsInternal = (0, immutable_1.List)([]);
            return;
        }
        let numerators = (0, utils_1.asImmutableList)(unitOrOptions?.numeratorUnits ?? []);
        const unsimplifiedDenominators = unitOrOptions?.denominatorUnits ?? [];
        const denominators = [];
        for (const denominator of unsimplifiedDenominators) {
            let simplifiedAway = false;
            for (const [i, numerator] of numerators.entries()) {
                const factor = conversionFactor(denominator, numerator);
                if (factor === null)
                    continue;
                value /= factor;
                numerators = numerators.delete(i);
                simplifiedAway = true;
                break;
            }
            if (!simplifiedAway)
                denominators.push(denominator);
        }
        this.valueInternal = value;
        this.numeratorUnitsInternal = numerators;
        this.denominatorUnitsInternal = (0, immutable_1.List)(denominators);
    }
    /** `this`'s value. */
    get value() {
        return this.valueInternal;
    }
    /** Whether `value` is an integer. */
    get isInt() {
        return (0, utils_2.fuzzyIsInt)(this.value);
    }
    /**
     * If `value` is an integer according to `isInt`, returns `value` rounded to
     * that integer.
     *
     * Otherwise, returns null.
     */
    get asInt() {
        return (0, utils_2.fuzzyAsInt)(this.value);
    }
    /** `this`'s numerator units. */
    get numeratorUnits() {
        return this.numeratorUnitsInternal;
    }
    /** `this`'s denominator units. */
    get denominatorUnits() {
        return this.denominatorUnitsInternal;
    }
    /** Whether `this` has any units. */
    get hasUnits() {
        return !(this.numeratorUnits.isEmpty() && this.denominatorUnits.isEmpty());
    }
    assertNumber() {
        return this;
    }
    /**
     * If `value` is an integer according to `isInt`, returns it as an integer.
     *
     * Otherwise, throws an error.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    assertInt(name) {
        const int = (0, utils_2.fuzzyAsInt)(this.value);
        if (int === null) {
            throw (0, utils_1.valueError)(`${this} is not an int`, name);
        }
        return int;
    }
    /**
     * If `value` is within `min` and `max`, returns `value`, or if it
     * `fuzzyEquals` `min` or `max`, returns `value` clamped to that value.
     *
     * Otherwise, throws an error.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    assertInRange(min, max, name) {
        const clamped = (0, utils_2.fuzzyInRange)(this.value, min, max);
        if (clamped === null) {
            throw (0, utils_1.valueError)(`${this} must be between ${min} and ${max}`, name);
        }
        return clamped;
    }
    /**
     * If `this` has no units, returns `this`.
     *
     * Otherwise, throws an error.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    assertNoUnits(name) {
        if (this.hasUnits) {
            throw (0, utils_1.valueError)(`Expected ${this} to have no units`, name);
        }
        return this;
    }
    /**
     * If `this` has `unit` as its only unit (and as a numerator), returns `this`.
     *
     * Otherwise, throws an error.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    assertUnit(unit, name) {
        if (!this.hasUnit(unit)) {
            throw (0, utils_1.valueError)(`Expected ${this} to have no unit ${unit}`, name);
        }
        return this;
    }
    /** Whether `this` has `unit` as its only unit (and as a numerator). */
    hasUnit(unit) {
        return (this.denominatorUnits.isEmpty() &&
            this.numeratorUnits.size === 1 &&
            this.numeratorUnits.get(0) === unit);
    }
    /** Whether `this` is compatible with `unit`. */
    compatibleWithUnit(unit) {
        if (!this.denominatorUnits.isEmpty())
            return false;
        if (this.numeratorUnits.size > 1)
            return false;
        const numerator = this.numeratorUnits.get(0);
        return typesByUnit[numerator]
            ? typesByUnit[numerator] === typesByUnit[unit]
            : numerator === unit;
    }
    /**
     * Returns a copy of `this`, converted to the units represented by
     * `newNumerators` and `newDenominators`.
     *
     * Throws an error if `this`'s units are incompatible with `newNumerators` and
     * `newDenominators`. Also throws an error if `this` is unitless and either
     * `newNumerators` or `newDenominators` are not empty, or vice-versa.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    convert(newNumerators, newDenominators, name) {
        return new SassNumber(this.convertValue(newNumerators, newDenominators, name), { numeratorUnits: newNumerators, denominatorUnits: newDenominators });
    }
    /**
     * Returns `value`, converted to the units represented by `newNumerators` and
     * `newDenominators`.
     *
     * Throws an error if `this`'s units are incompatible with `newNumerators` and
     * `newDenominators`. Also throws an error if `this` is unitless and either
     * `newNumerators` or `newDenominators` are not empty, or vice-versa.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    convertValue(newNumerators, newDenominators, name) {
        return this.convertOrCoerce({
            coerceUnitless: false,
            newNumeratorUnits: (0, utils_1.asImmutableList)(newNumerators),
            newDenominatorUnits: (0, utils_1.asImmutableList)(newDenominators),
            name,
        });
    }
    /**
     * Returns a copy of `this`, converted to the same units as `other`.
     *
     * Throws an error if `this`'s units are incompatible with `other`'s units, or
     * if either number is unitless but the other is not.
     *
     * If `this` came from a function argument, `name` is the argument name
     * and `otherName` is the argument name for `other` (both without the `$`).
     * They are used for error reporting.
     */
    convertToMatch(other, name, otherName) {
        return new SassNumber(this.convertValueToMatch(other, name, otherName), {
            numeratorUnits: other.numeratorUnits,
            denominatorUnits: other.denominatorUnits,
        });
    }
    /**
     * Returns `value`, converted to the same units as `other`.
     *
     * Throws an error if `this`'s units are incompatible with `other`'s units, or
     * if either number is unitless but the other is not.
     *
     * If `this` came from a function argument, `name` is the argument name
     * and `otherName` is the argument name for `other` (both without the `$`).
     * They are used for error reporting.
     */
    convertValueToMatch(other, name, otherName) {
        return this.convertOrCoerce({
            coerceUnitless: false,
            other,
            name,
            otherName,
        });
    }
    /**
     * Returns a copy of `this`, converted to the units represented by
     * `newNumerators` and `newDenominators`.
     *
     * Does *not* throw an error if this number is unitless and either
     * `newNumerators` or `newDenominators` are not empty, or vice-versa. Instead,
     * it treats all unitless numbers as convertible to and from all units
     * without changing the value.
     *
     * Throws an error if `this`'s units are incompatible with `newNumerators` and
     * `newDenominators`.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    coerce(newNumerators, newDenominators, name) {
        return new SassNumber(this.coerceValue(newNumerators, newDenominators, name), { numeratorUnits: newNumerators, denominatorUnits: newDenominators });
    }
    /**
     * Returns `value`, converted to the units represented by `newNumerators` and
     * `newDenominators`.
     *
     * Does *not* throw an error if this number is unitless and either
     * `newNumerators` or `newDenominators` are not empty, or vice-versa. Instead,
     * it treats all unitless numbers as convertible to and from all units
     * without changing the value.
     *
     * Throws an error if `this`'s units are incompatible with `newNumerators` and
     * `newDenominators`.
     *
     * If `this` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    coerceValue(newNumerators, newDenominators, name) {
        return this.convertOrCoerce({
            coerceUnitless: true,
            newNumeratorUnits: (0, utils_1.asImmutableList)(newNumerators),
            newDenominatorUnits: (0, utils_1.asImmutableList)(newDenominators),
            name,
        });
    }
    /**
     * Returns a copy of `this`, converted to the same units as `other`.
     *
     * Does *not* throw an error if `this` is unitless and `other` is not, or
     * vice-versa. Instead, it treats all unitless numbers as convertible to and
     * from all units without changing the value.
     *
     * Throws an error if `this`'s units are incompatible with `other`'s units.
     *
     * If `this` came from a function argument, `name` is the argument name
     * and `otherName` is the argument name for `other` (both without the `$`).
     * They are used for error reporting.
     */
    coerceToMatch(other, name, otherName) {
        return new SassNumber(this.coerceValueToMatch(other, name, otherName), {
            numeratorUnits: other.numeratorUnits,
            denominatorUnits: other.denominatorUnits,
        });
    }
    /**
     * Returns `value`, converted to the same units as `other`.
     *
     * Does *not* throw an error if `this` is unitless and `other` is not, or
     * vice-versa. Instead, it treats all unitless numbers as convertible to and
     * from all units without changing the value.
     *
     * Throws an error if `this`'s units are incompatible with `other`'s units.
     *
     * If `this` came from a function argument, `name` is the argument name
     * and `otherName` is the argument name for `other` (both without the `$`).
     * They are used for error reporting.
     */
    coerceValueToMatch(other, name, otherName) {
        return this.convertOrCoerce({
            coerceUnitless: true,
            other,
            name,
            otherName,
        });
    }
    equals(other) {
        if (!(other instanceof SassNumber))
            return false;
        try {
            return (0, utils_2.fuzzyEquals)(this.value, other.convertValueToMatch(this));
        }
        catch {
            return false;
        }
    }
    hashCode() {
        const canonicalNumerators = canonicalizeUnits(this.numeratorUnits);
        const canonicalDenominators = canonicalizeUnits(this.denominatorUnits);
        const canonicalValue = this.convertValue(canonicalNumerators, canonicalDenominators);
        return ((0, utils_2.fuzzyHashCode)(canonicalValue) ^
            (0, immutable_1.hash)(canonicalNumerators) ^
            (0, immutable_1.hash)(canonicalDenominators));
    }
    toString() {
        return `${this.value}${unitString(this.numeratorUnits, this.denominatorUnits)}`;
    }
    // Returns the value of converting `number` to new units.
    //
    // The units may be specified as lists of units (`newNumeratorUnits` and
    // `newDenominatorUnits`), or by providng a SassNumber `other` that contains the
    // desired units.
    //
    // Throws an error if `number` is not compatible with the new units. Coercing a
    // unitful number to unitless (or vice-versa) throws an error unless
    // specifically enabled with `coerceUnitless`.
    convertOrCoerce(params) {
        const newNumerators = 'other' in params
            ? params.other.numeratorUnits
            : params.newNumeratorUnits;
        const newDenominators = 'other' in params
            ? params.other.denominatorUnits
            : params.newDenominatorUnits;
        const compatibilityError = () => {
            if ('other' in params) {
                let message = `${this} and`;
                if (params.otherName) {
                    message += ` $${params.otherName}:`;
                }
                message += ` ${params.other} have incompatible units`;
                if (!this.hasUnits || !otherHasUnits) {
                    message += " (one has units and the other doesn't)";
                }
                return (0, utils_1.valueError)(message, params.name);
            }
            if (!otherHasUnits) {
                return (0, utils_1.valueError)(`Expected ${this} to have no units.`, params.name);
            }
            // For single numerators, throw a detailed error with info about which unit
            // types would have been acceptable.
            if (newNumerators.size === 1 && newDenominators.isEmpty()) {
                const type = typesByUnit[newNumerators.get(0)];
                if (type) {
                    return (0, utils_1.valueError)(`Expected ${this} to have a single ${type} unit (${unitsByType[type].join(', ')}).`, params.name);
                }
            }
            const unitSize = newNumerators.size + newDenominators.size;
            return (0, utils_1.valueError)(`Expected $this to have ${unitSize === 0
                ? 'no units'
                : `unit${unitSize > 1 ? 's' : ''} ${unitString(newNumerators, newDenominators)}`}.`, params.name);
        };
        const otherHasUnits = !newNumerators.isEmpty() || !newDenominators.isEmpty();
        if ((this.hasUnits && !otherHasUnits) ||
            (!this.hasUnits && otherHasUnits)) {
            if (params.coerceUnitless)
                return this.value;
            throw compatibilityError();
        }
        if (this.numeratorUnits.equals(newNumerators) &&
            this.denominatorUnits.equals(newDenominators)) {
            return this.value;
        }
        let value = this.value;
        let oldNumerators = this.numeratorUnits;
        for (const newNumerator of newNumerators) {
            const idx = oldNumerators.findIndex(oldNumerator => {
                const factor = conversionFactor(oldNumerator, newNumerator);
                if (factor === null)
                    return false;
                value *= factor;
                return true;
            });
            if (idx < 0)
                throw compatibilityError();
            oldNumerators = oldNumerators.delete(idx);
        }
        let oldDenominators = this.denominatorUnits;
        for (const newDenominator of newDenominators) {
            const idx = oldDenominators.findIndex(oldDenominator => {
                const factor = conversionFactor(oldDenominator, newDenominator);
                if (factor === null)
                    return false;
                value /= factor;
                return true;
            });
            if (idx < 0)
                throw compatibilityError();
            oldDenominators = oldDenominators.delete(idx);
        }
        if (!oldNumerators.isEmpty() || !oldDenominators.isEmpty()) {
            throw compatibilityError();
        }
        return value;
    }
}
exports.SassNumber = SassNumber;
// Returns the conversion factor needed to convert from `fromUnit` to `toUnit`.
// Returns null if no such factor exists.
function conversionFactor(fromUnit, toUnit) {
    if (fromUnit === toUnit)
        return 1;
    const factors = conversions[toUnit];
    if (!factors)
        return null;
    return factors[fromUnit] ?? null;
}
// Returns a human-readable string representation of `numerators` and
// `denominators`.
function unitString(numerators, denominators) {
    if (numerators.isEmpty() && denominators.isEmpty()) {
        return '';
    }
    if (denominators.isEmpty()) {
        return numerators.join('*');
    }
    if (numerators.isEmpty()) {
        return denominators.size === 1
            ? `${denominators.get(0)}^-1`
            : `(${denominators.join('*')})^-1`;
    }
    return `${numerators.join('*')}/${denominators.join('*')}`;
}
// Converts the `units` list into an equivalent canonical list.
function canonicalizeUnits(units) {
    return units
        .map(unit => {
        const type = typesByUnit[unit];
        return type ? unitsByType[type][0] : unit;
    })
        .sort();
}
//# sourceMappingURL=number.js.map