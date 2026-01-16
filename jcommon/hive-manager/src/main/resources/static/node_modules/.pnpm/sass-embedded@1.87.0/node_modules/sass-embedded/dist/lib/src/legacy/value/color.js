"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyColor = void 0;
const utils_1 = require("../../utils");
const color_1 = require("../../value/color");
const base_1 = require("./base");
class LegacyColor extends base_1.LegacyValueBase {
    constructor(redOrArgb, green, blue, alpha) {
        if (redOrArgb instanceof color_1.SassColor) {
            super(redOrArgb);
            return;
        }
        let red;
        if ((0, utils_1.isNullOrUndefined)(green) || (0, utils_1.isNullOrUndefined)(blue)) {
            const argb = redOrArgb;
            alpha = (argb >> 24) / 0xff;
            red = (argb >> 16) % 0x100;
            green = (argb >> 8) % 0x100;
            blue = argb % 0x100;
        }
        else {
            red = redOrArgb;
        }
        super(new color_1.SassColor({
            red: clamp(red, 0, 255),
            green: clamp(green, 0, 255),
            blue: clamp(blue, 0, 255),
            alpha: alpha ? clamp(alpha, 0, 1) : 1,
        }));
    }
    getR() {
        return this.inner.red;
    }
    setR(value) {
        this.inner = this.inner.change({ red: clamp(value, 0, 255) });
    }
    getG() {
        return this.inner.green;
    }
    setG(value) {
        this.inner = this.inner.change({ green: clamp(value, 0, 255) });
    }
    getB() {
        return this.inner.blue;
    }
    setB(value) {
        this.inner = this.inner.change({ blue: clamp(value, 0, 255) });
    }
    getA() {
        return this.inner.alpha;
    }
    setA(value) {
        this.inner = this.inner.change({ alpha: clamp(value, 0, 1) });
    }
}
exports.LegacyColor = LegacyColor;
Object.defineProperty(LegacyColor, 'name', { value: 'sass.types.Color' });
// Returns `number` clamped to between `min` and `max`.
function clamp(num, min, max) {
    return Math.min(Math.max(num, min), max);
}
//# sourceMappingURL=color.js.map