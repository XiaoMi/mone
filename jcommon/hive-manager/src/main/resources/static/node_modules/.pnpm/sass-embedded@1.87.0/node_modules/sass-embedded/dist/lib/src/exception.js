"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.Exception = void 0;
const deprotofy_span_1 = require("./deprotofy-span");
class Exception extends Error {
    sassMessage;
    sassStack;
    span;
    constructor(failure) {
        super(failure.formatted);
        this.sassMessage = failure.message;
        this.sassStack = failure.stackTrace;
        this.span = (0, deprotofy_span_1.deprotofySourceSpan)(failure.span);
    }
    toString() {
        return this.message;
    }
}
exports.Exception = Exception;
//# sourceMappingURL=exception.js.map