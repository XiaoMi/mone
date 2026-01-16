"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.wrapFunction = wrapFunction;
exports.unwrapValue = unwrapValue;
exports.wrapValue = wrapValue;
const util = require("util");
const base_1 = require("./base");
const color_1 = require("./color");
const list_1 = require("./list");
const map_1 = require("./map");
const number_1 = require("./number");
const string_1 = require("./string");
const value_1 = require("../../value");
const color_2 = require("../../value/color");
const list_2 = require("../../value/list");
const map_2 = require("../../value/map");
const number_2 = require("../../value/number");
const string_2 = require("../../value/string");
/**
 * Converts a `LegacyFunction` into a `CustomFunction` so it can be passed to
 * the new JS API.
 */
function wrapFunction(thisArg, callback, sync) {
    if (sync) {
        return args => unwrapTypedValue(callback.apply(thisArg, args.map(wrapValue)));
    }
    else {
        return args => new Promise((resolve, reject) => {
            function done(result) {
                try {
                    if (result instanceof Error) {
                        reject(result);
                    }
                    else {
                        resolve(unwrapTypedValue(result));
                    }
                }
                catch (error) {
                    reject(error);
                }
            }
            // The cast here is necesary to work around microsoft/TypeScript#33815.
            const syncResult = callback.apply(thisArg, [...args.map(wrapValue), done]);
            if (syncResult !== undefined)
                resolve(unwrapTypedValue(syncResult));
        });
    }
}
// Like `unwrapValue()`, but returns a `types.Value` type.
function unwrapTypedValue(value) {
    return unwrapValue(value);
}
/** Converts a value returned by a `LegacyFunction` into a `Value`. */
function unwrapValue(value) {
    if (value instanceof Error)
        throw value;
    if (value instanceof value_1.Value)
        return value;
    if (value instanceof base_1.LegacyValueBase)
        return value.inner;
    throw new Error(`Expected legacy Sass value, got ${util.inspect(value)}.`);
}
/** Converts a `Value` into a `LegacyValue`. */
function wrapValue(value) {
    if (value instanceof color_2.SassColor)
        return new color_1.LegacyColor(value);
    if (value instanceof list_2.SassList)
        return new list_1.LegacyList(value);
    if (value instanceof map_2.SassMap)
        return new map_1.LegacyMap(value);
    if (value instanceof number_2.SassNumber)
        return new number_1.LegacyNumber(value);
    if (value instanceof string_2.SassString)
        return new string_1.LegacyString(value);
    if (value instanceof value_1.Value)
        return value;
    throw new Error(`Expected Sass value, got ${util.inspect(value)}.`);
}
//# sourceMappingURL=wrap.js.map