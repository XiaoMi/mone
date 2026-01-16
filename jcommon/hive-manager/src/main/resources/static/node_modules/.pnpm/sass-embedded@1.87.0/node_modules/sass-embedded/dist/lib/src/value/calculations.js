"use strict";
// Copyright 2023 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.CalculationInterpolation = exports.CalculationOperation = exports.SassCalculation = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
const string_1 = require("./string");
function assertCalculationValue(value) {
    if (value instanceof string_1.SassString && value.hasQuotes) {
        throw new Error(`Expected ${value} to be an unquoted string.`);
    }
}
function isValidClampArg(value) {
    return (value instanceof CalculationInterpolation ||
        (value instanceof string_1.SassString && !value.hasQuotes));
}
/* A SassScript calculation */
class SassCalculation extends index_1.Value {
    name;
    arguments;
    constructor(name, args) {
        super();
        this.name = name;
        this.arguments = (0, immutable_1.List)(args);
    }
    static calc(argument) {
        assertCalculationValue(argument);
        return new SassCalculation('calc', [argument]);
    }
    static min(args) {
        args.forEach(assertCalculationValue);
        return new SassCalculation('min', args);
    }
    static max(args) {
        args.forEach(assertCalculationValue);
        return new SassCalculation('max', args);
    }
    static clamp(min, value, max) {
        if ((value === undefined && !isValidClampArg(min)) ||
            (max === undefined && ![min, value].some(x => x && isValidClampArg(x)))) {
            throw new Error('Argument must be an unquoted SassString or CalculationInterpolation.');
        }
        const args = [min];
        if (value !== undefined)
            args.push(value);
        if (max !== undefined)
            args.push(max);
        args.forEach(assertCalculationValue);
        return new SassCalculation('clamp', args);
    }
    assertCalculation() {
        return this;
    }
    equals(other) {
        return (other instanceof SassCalculation &&
            this.name === other.name &&
            this.arguments.equals(other.arguments));
    }
    hashCode() {
        return (0, immutable_1.hash)(this.name) ^ this.arguments.hashCode();
    }
    toString() {
        return `${this.name}(${this.arguments.join(', ')})`;
    }
}
exports.SassCalculation = SassCalculation;
const operators = ['+', '-', '*', '/'];
class CalculationOperation {
    operator;
    left;
    right;
    constructor(operator, left, right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        if (!operators.includes(operator)) {
            throw new Error(`Invalid operator: ${operator}`);
        }
        assertCalculationValue(left);
        assertCalculationValue(right);
    }
    equals(other) {
        return (other instanceof CalculationOperation &&
            this.operator === other.operator &&
            this.left === other.left &&
            this.right === other.right);
    }
    hashCode() {
        return (0, immutable_1.hash)(this.operator) ^ (0, immutable_1.hash)(this.left) ^ (0, immutable_1.hash)(this.right);
    }
}
exports.CalculationOperation = CalculationOperation;
class CalculationInterpolation {
    value;
    constructor(value) {
        this.value = value;
    }
    equals(other) {
        return (other instanceof CalculationInterpolation && this.value === other.value);
    }
    hashCode() {
        return (0, immutable_1.hash)(this.value);
    }
}
exports.CalculationInterpolation = CalculationInterpolation;
//# sourceMappingURL=calculations.js.map