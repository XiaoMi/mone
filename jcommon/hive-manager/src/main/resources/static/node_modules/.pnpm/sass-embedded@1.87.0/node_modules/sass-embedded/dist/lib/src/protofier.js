"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.Protofier = void 0;
const immutable_1 = require("immutable");
const protobuf_1 = require("@bufbuild/protobuf");
const proto = require("./vendor/embedded_sass_pb");
const utils = require("./utils");
const argument_list_1 = require("./value/argument-list");
const color_1 = require("./value/color");
const function_1 = require("./value/function");
const list_1 = require("./value/list");
const map_1 = require("./value/map");
const number_1 = require("./value/number");
const string_1 = require("./value/string");
const null_1 = require("./value/null");
const boolean_1 = require("./value/boolean");
const calculations_1 = require("./value/calculations");
const mixin_1 = require("./value/mixin");
/**
 * A class that converts [Value] objects into protobufs.
 *
 * A given [Protofier] instance is valid only within the scope of a single
 * custom function call.
 */
class Protofier {
    functions;
    /** All the argument lists returned by `deprotofy()`. */
    argumentLists = [];
    /**
     * Returns IDs of all argument lists passed to `deprotofy()` whose keywords
     * have been accessed.
     */
    get accessedArgumentLists() {
        return this.argumentLists
            .filter(list => list.keywordsAccessed)
            .map(list => list.id);
    }
    constructor(
    /**
     * The registry of custom functions that can be invoked by the compiler.
     * This is used to register first-class functions so that the compiler may
     * invoke them.
     */
    functions) {
        this.functions = functions;
    }
    /** Converts `value` to its protocol buffer representation. */
    protofy(value) {
        const result = (0, protobuf_1.create)(proto.ValueSchema, {});
        if (value instanceof string_1.SassString) {
            const string = (0, protobuf_1.create)(proto.Value_StringSchema, {
                text: value.text,
                quoted: value.hasQuotes,
            });
            result.value = { case: 'string', value: string };
        }
        else if (value instanceof number_1.SassNumber) {
            result.value = { case: 'number', value: this.protofyNumber(value) };
        }
        else if (value instanceof color_1.SassColor) {
            const channels = value.channelsOrNull;
            const color = (0, protobuf_1.create)(proto.Value_ColorSchema, {
                channel1: channels.get(0),
                channel2: channels.get(1),
                channel3: channels.get(2),
                alpha: value.isChannelMissing('alpha') ? undefined : value.alpha,
                space: value.space,
            });
            result.value = { case: 'color', value: color };
        }
        else if (value instanceof list_1.SassList) {
            const list = (0, protobuf_1.create)(proto.Value_ListSchema, {
                separator: this.protofySeparator(value.separator),
                hasBrackets: value.hasBrackets,
                contents: value.asList.map(element => this.protofy(element)).toArray(),
            });
            result.value = { case: 'list', value: list };
        }
        else if (value instanceof argument_list_1.SassArgumentList) {
            const list = (0, protobuf_1.create)(proto.Value_ArgumentListSchema, {
                id: value.id,
                separator: this.protofySeparator(value.separator),
                contents: value.asList.map(element => this.protofy(element)).toArray(),
            });
            for (const [key, mapValue] of value.keywordsInternal) {
                list.keywords[key] = this.protofy(mapValue);
            }
            result.value = { case: 'argumentList', value: list };
        }
        else if (value instanceof map_1.SassMap) {
            const map = (0, protobuf_1.create)(proto.Value_MapSchema, {
                entries: value.contents.toArray().map(([key, value]) => ({
                    key: this.protofy(key),
                    value: this.protofy(value),
                })),
            });
            result.value = { case: 'map', value: map };
        }
        else if (value instanceof function_1.SassFunction) {
            if (value.id !== undefined) {
                const fn = (0, protobuf_1.create)(proto.Value_CompilerFunctionSchema, value);
                result.value = { case: 'compilerFunction', value: fn };
            }
            else {
                const fn = (0, protobuf_1.create)(proto.Value_HostFunctionSchema, {
                    id: this.functions.register(value.callback),
                    signature: value.signature,
                });
                result.value = { case: 'hostFunction', value: fn };
            }
        }
        else if (value instanceof mixin_1.SassMixin) {
            const mixin = (0, protobuf_1.create)(proto.Value_CompilerMixinSchema, value);
            result.value = { case: 'compilerMixin', value: mixin };
        }
        else if (value instanceof calculations_1.SassCalculation) {
            result.value = {
                case: 'calculation',
                value: this.protofyCalculation(value),
            };
        }
        else if (value === boolean_1.sassTrue) {
            result.value = { case: 'singleton', value: proto.SingletonValue.TRUE };
        }
        else if (value === boolean_1.sassFalse) {
            result.value = { case: 'singleton', value: proto.SingletonValue.FALSE };
        }
        else if (value === null_1.sassNull) {
            result.value = { case: 'singleton', value: proto.SingletonValue.NULL };
        }
        else {
            throw utils.compilerError(`Unknown Value ${value}`);
        }
        return result;
    }
    /** Converts `number` to its protocol buffer representation. */
    protofyNumber(number) {
        return (0, protobuf_1.create)(proto.Value_NumberSchema, {
            value: number.value,
            numerators: number.numeratorUnits.toArray(),
            denominators: number.denominatorUnits.toArray(),
        });
    }
    /** Converts `separator` to its protocol buffer representation. */
    protofySeparator(separator) {
        switch (separator) {
            case ',':
                return proto.ListSeparator.COMMA;
            case ' ':
                return proto.ListSeparator.SPACE;
            case '/':
                return proto.ListSeparator.SLASH;
            case null:
                return proto.ListSeparator.UNDECIDED;
            default:
                throw utils.compilerError(`Unknown ListSeparator ${separator}`);
        }
    }
    /** Converts `calculation` to its protocol buffer representation. */
    protofyCalculation(calculation) {
        return (0, protobuf_1.create)(proto.Value_CalculationSchema, {
            name: calculation.name,
            arguments: calculation.arguments
                .map(this.protofyCalculationValue.bind(this))
                .toArray(),
        });
    }
    /** Converts a CalculationValue that appears within a `SassCalculation` to
     * its protocol buffer representation. */
    protofyCalculationValue(value) {
        const result = (0, protobuf_1.create)(proto.Value_Calculation_CalculationValueSchema, {});
        if (value instanceof calculations_1.SassCalculation) {
            result.value = {
                case: 'calculation',
                value: this.protofyCalculation(value),
            };
        }
        else if (value instanceof calculations_1.CalculationOperation) {
            result.value = {
                case: 'operation',
                value: (0, protobuf_1.create)(proto.Value_Calculation_CalculationOperationSchema, {
                    operator: this.protofyCalculationOperator(value.operator),
                    left: this.protofyCalculationValue(value.left),
                    right: this.protofyCalculationValue(value.right),
                }),
            };
        }
        else if (value instanceof calculations_1.CalculationInterpolation) {
            result.value = { case: 'interpolation', value: value.value };
        }
        else if (value instanceof string_1.SassString) {
            result.value = { case: 'string', value: value.text };
        }
        else if (value instanceof number_1.SassNumber) {
            result.value = { case: 'number', value: this.protofyNumber(value) };
        }
        else {
            throw utils.compilerError(`Unknown CalculationValue ${value}`);
        }
        return result;
    }
    /** Converts `operator` to its protocol buffer representation. */
    protofyCalculationOperator(operator) {
        switch (operator) {
            case '+':
                return proto.CalculationOperator.PLUS;
            case '-':
                return proto.CalculationOperator.MINUS;
            case '*':
                return proto.CalculationOperator.TIMES;
            case '/':
                return proto.CalculationOperator.DIVIDE;
            default:
                throw utils.compilerError(`Unknown CalculationOperator ${operator}`);
        }
    }
    /** Converts `value` to its JS representation. */
    deprotofy(value) {
        switch (value.value.case) {
            case 'string': {
                const string = value.value.value;
                return string.text.length === 0
                    ? string_1.SassString.empty({ quotes: string.quoted })
                    : new string_1.SassString(string.text, { quotes: string.quoted });
            }
            case 'number': {
                return this.deprotofyNumber(value.value.value);
            }
            case 'color': {
                const color = value.value.value;
                const channel1 = color.channel1 ?? null;
                const channel2 = color.channel2 ?? null;
                const channel3 = color.channel3 ?? null;
                const alpha = color.alpha ?? null;
                const space = color.space;
                switch (color.space.toLowerCase()) {
                    case 'rgb':
                    case 'srgb':
                    case 'srgb-linear':
                    case 'display-p3':
                    case 'a98-rgb':
                    case 'prophoto-rgb':
                    case 'rec2020':
                        return new color_1.SassColor({
                            red: channel1,
                            green: channel2,
                            blue: channel3,
                            alpha,
                            space,
                        });
                    case 'hsl':
                        return new color_1.SassColor({
                            hue: channel1,
                            saturation: channel2,
                            lightness: channel3,
                            alpha,
                            space,
                        });
                    case 'hwb':
                        return new color_1.SassColor({
                            hue: channel1,
                            whiteness: channel2,
                            blackness: channel3,
                            alpha,
                            space,
                        });
                    case 'lab':
                    case 'oklab':
                        return new color_1.SassColor({
                            lightness: channel1,
                            a: channel2,
                            b: channel3,
                            alpha,
                            space,
                        });
                    case 'lch':
                    case 'oklch':
                        return new color_1.SassColor({
                            lightness: channel1,
                            chroma: channel2,
                            hue: channel3,
                            alpha,
                            space,
                        });
                    case 'xyz':
                    case 'xyz-d65':
                    case 'xyz-d50':
                        return new color_1.SassColor({
                            x: channel1,
                            y: channel2,
                            z: channel3,
                            alpha,
                            space,
                        });
                    default:
                        throw utils.compilerError(`Unknown color space "${color.space}".`);
                }
            }
            case 'list': {
                const list = value.value.value;
                const separator = this.deprotofySeparator(list.separator);
                if (separator === null && list.contents.length > 1) {
                    throw utils.compilerError(`Value.List ${list} can't have an undecided separator because it ` +
                        `has ${list.contents.length} elements`);
                }
                return new list_1.SassList(list.contents.map(element => this.deprotofy(element)), { separator, brackets: list.hasBrackets });
            }
            case 'argumentList': {
                const list = value.value.value;
                const separator = this.deprotofySeparator(list.separator);
                if (separator === null && list.contents.length > 1) {
                    throw utils.compilerError(`Value.List ${list} can't have an undecided separator because it ` +
                        `has ${list.contents.length} elements`);
                }
                const result = new argument_list_1.SassArgumentList(list.contents.map(element => this.deprotofy(element)), (0, immutable_1.OrderedMap)(Object.entries(list.keywords).map(([key, value]) => [
                    key,
                    this.deprotofy(value),
                ])), separator, list.id);
                this.argumentLists.push(result);
                return result;
            }
            case 'map':
                return new map_1.SassMap((0, immutable_1.OrderedMap)(value.value.value.entries.map(entry => {
                    const key = entry.key;
                    if (!key)
                        throw utils.mandatoryError('Value.Map.Entry.key');
                    const value = entry.value;
                    if (!value)
                        throw utils.mandatoryError('Value.Map.Entry.value');
                    return [this.deprotofy(key), this.deprotofy(value)];
                })));
            case 'compilerFunction':
                return new function_1.SassFunction(value.value.value.id);
            case 'hostFunction':
                throw utils.compilerError('The compiler may not send Value.host_function.');
            case 'compilerMixin':
                return new mixin_1.SassMixin(value.value.value.id);
            case 'calculation':
                return this.deprotofyCalculation(value.value.value);
            case 'singleton':
                switch (value.value.value) {
                    case proto.SingletonValue.TRUE:
                        return boolean_1.sassTrue;
                    case proto.SingletonValue.FALSE:
                        return boolean_1.sassFalse;
                    case proto.SingletonValue.NULL:
                        return null_1.sassNull;
                }
            // eslint-disable-next-line no-fallthrough
            default:
                throw utils.mandatoryError('Value.value');
        }
    }
    /** Converts `number` to its JS representation. */
    deprotofyNumber(number) {
        return new number_1.SassNumber(number.value, {
            numeratorUnits: number.numerators,
            denominatorUnits: number.denominators,
        });
    }
    /** Converts `separator` to its JS representation. */
    deprotofySeparator(separator) {
        switch (separator) {
            case proto.ListSeparator.COMMA:
                return ',';
            case proto.ListSeparator.SPACE:
                return ' ';
            case proto.ListSeparator.SLASH:
                return '/';
            case proto.ListSeparator.UNDECIDED:
                return null;
            default:
                throw utils.compilerError(`Unknown separator ${separator}`);
        }
    }
    /** Converts `calculation` to its Sass representation. */
    deprotofyCalculation(calculation) {
        switch (calculation.name) {
            case 'calc':
                if (calculation.arguments.length !== 1) {
                    throw utils.compilerError('Value.Calculation.arguments must have exactly one argument for calc().');
                }
                return calculations_1.SassCalculation.calc(this.deprotofyCalculationValue(calculation.arguments[0]));
            case 'clamp':
                if (calculation.arguments.length === 0 ||
                    calculation.arguments.length > 3) {
                    throw utils.compilerError('Value.Calculation.arguments must have 1 to 3 arguments for clamp().');
                }
                return calculations_1.SassCalculation.clamp(this.deprotofyCalculationValue(calculation.arguments[0]), calculation.arguments.length > 1
                    ? this.deprotofyCalculationValue(calculation.arguments[1])
                    : undefined, calculation.arguments.length > 2
                    ? this.deprotofyCalculationValue(calculation.arguments[2])
                    : undefined);
            case 'min':
                if (calculation.arguments.length === 0) {
                    throw utils.compilerError('Value.Calculation.arguments must have at least 1 argument for min().');
                }
                return calculations_1.SassCalculation.min(calculation.arguments.map(this.deprotofyCalculationValue));
            case 'max':
                if (calculation.arguments.length === 0) {
                    throw utils.compilerError('Value.Calculation.arguments must have at least 1 argument for max().');
                }
                return calculations_1.SassCalculation.max(calculation.arguments.map(this.deprotofyCalculationValue));
            default:
                throw utils.compilerError(`Value.Calculation.name "${calculation.name}" is not a recognized calculation type.`);
        }
    }
    /** Converts `value` to its Sass representation. */
    deprotofyCalculationValue(value) {
        switch (value.value.case) {
            case 'number':
                return this.deprotofyNumber(value.value.value);
            case 'calculation':
                return this.deprotofyCalculation(value.value.value);
            case 'string':
                return new string_1.SassString(value.value.value, { quotes: false });
            case 'operation':
                return new calculations_1.CalculationOperation(this.deprotofyCalculationOperator(value.value.value.operator), this.deprotofyCalculationValue(value.value.value.left), this.deprotofyCalculationValue(value.value.value.right));
            case 'interpolation':
                return new calculations_1.CalculationInterpolation(value.value.value);
            default:
                throw utils.mandatoryError('Calculation.CalculationValue.value');
        }
    }
    /** Converts `operator` to its Sass representation. */
    deprotofyCalculationOperator(operator) {
        switch (operator) {
            case proto.CalculationOperator.PLUS:
                return '+';
            case proto.CalculationOperator.MINUS:
                return '-';
            case proto.CalculationOperator.TIMES:
                return '*';
            case proto.CalculationOperator.DIVIDE:
                return '/';
            default:
                throw utils.compilerError(`Unknown CalculationOperator ${operator}`);
        }
    }
}
exports.Protofier = Protofier;
//# sourceMappingURL=protofier.js.map