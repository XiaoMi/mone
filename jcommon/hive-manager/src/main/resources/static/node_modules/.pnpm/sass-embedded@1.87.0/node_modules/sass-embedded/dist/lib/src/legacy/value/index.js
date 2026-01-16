"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.Error = exports.String = exports.Number = exports.Null = exports.Map = exports.List = exports.Color = exports.Boolean = void 0;
const boolean_1 = require("../../value/boolean");
const null_1 = require("../../value/null");
const color_1 = require("./color");
const list_1 = require("./list");
const map_1 = require("./map");
const number_1 = require("./number");
const string_1 = require("./string");
exports.Boolean = boolean_1.SassBooleanInternal;
exports.Color = color_1.LegacyColor;
exports.List = list_1.LegacyList;
exports.Map = map_1.LegacyMap;
exports.Null = null_1.SassNull;
exports.Number = number_1.LegacyNumber;
exports.String = string_1.LegacyString;
// For the `sass.types.Error` object, we just re-export the native Error class.
exports.Error = global.Error;
//# sourceMappingURL=index.js.map