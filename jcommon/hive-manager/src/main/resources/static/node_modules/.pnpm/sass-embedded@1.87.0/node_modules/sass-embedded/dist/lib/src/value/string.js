"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassString = void 0;
const immutable_1 = require("immutable");
const index_1 = require("./index");
const utils_1 = require("../utils");
/** A SassScript string. */
class SassString extends index_1.Value {
    textInternal;
    hasQuotesInternal;
    constructor(textOrOptions, options) {
        super();
        if (typeof textOrOptions === 'string') {
            this.textInternal = textOrOptions;
            this.hasQuotesInternal = options?.quotes ?? true;
        }
        else {
            this.textInternal = '';
            this.hasQuotesInternal = textOrOptions?.quotes ?? true;
        }
    }
    /** Creates an empty string, optionally with quotes. */
    static empty(options) {
        return options === undefined || options?.quotes
            ? emptyQuoted
            : emptyUnquoted;
    }
    /** `this`'s text. */
    get text() {
        return this.textInternal;
    }
    /** Whether `this` has quotes. */
    get hasQuotes() {
        return this.hasQuotesInternal;
    }
    assertString() {
        return this;
    }
    /**
     * Sass's notion of `this`'s length.
     *
     * Sass treats strings as a series of Unicode code points while JS treats them
     * as a series of UTF-16 code units. For example, the character U+1F60A,
     * Smiling Face With Smiling Eyes, is a single Unicode code point but is
     * represented in UTF-16 as two code units (`0xD83D` and `0xDE0A`). So in
     * JS, `"n😊b".length` returns `4`, whereas in Sass `string.length("n😊b")`
     * returns `3`.
     */
    get sassLength() {
        let length = 0;
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        for (const codepoint of this.text) {
            length++;
        }
        return length;
    }
    /**
     * Converts `sassIndex` to a JS index into `text`.
     *
     * Sass indices are one-based, while JS indices are zero-based. Sass
     * indices may also be negative in order to index from the end of the string.
     *
     * In addition, Sass indices refer to Unicode code points while JS string
     * indices refer to UTF-16 code units. For example, the character U+1F60A,
     * Smiling Face With Smiling Eyes, is a single Unicode code point but is
     * represented in UTF-16 as two code units (`0xD83D` and `0xDE0A`). So in
     * JS, `"n😊b".charAt(1)` returns `0xD83D`, whereas in Sass
     * `string.slice("n😊b", 1, 1)` returns `"😊"`.
     *
     * This function converts Sass's code point indices to JS's code unit
     * indices. This means it's O(n) in the length of `text`.
     *
     * Throws an error `sassIndex` isn't a number, if that number isn't an
     * integer, or if that integer isn't a valid index for this string.
     *
     * If `sassIndex` came from a function argument, `name` is the argument name
     * (without the `$`) and is used for error reporting.
     */
    sassIndexToStringIndex(sassIndex, name) {
        let sassIdx = sassIndex.assertNumber().assertInt();
        if (sassIdx === 0) {
            throw (0, utils_1.valueError)('String index may not be 0', name);
        }
        const sassLength = this.sassLength;
        if (Math.abs(sassIdx) > sassLength) {
            throw (0, utils_1.valueError)(`Invalid index ${sassIdx} for a string with ${sassLength} characters`, name);
        }
        if (sassIdx < 0)
            sassIdx += sassLength + 1;
        let pointer = 1;
        let idx = 0;
        for (const codePoint of this.text) {
            if (pointer === sassIdx)
                break;
            idx += codePoint.length;
            pointer++;
        }
        return idx;
    }
    equals(other) {
        return other instanceof SassString && this.text === other.text;
    }
    hashCode() {
        return (0, immutable_1.hash)(this.text);
    }
    toString() {
        return this.hasQuotes ? `"${this.text}"` : this.text;
    }
}
exports.SassString = SassString;
// A quoted empty string returned by `SassString.empty()`.
const emptyQuoted = new SassString('', { quotes: true });
// An unquoted empty string returned by `SassString.empty()`.
const emptyUnquoted = new SassString('', { quotes: false });
//# sourceMappingURL=string.js.map