"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassColor = void 0;
const index_1 = require("./index");
const deprecations_1 = require("../deprecations");
const utils_1 = require("../utils");
const utils_2 = require("./utils");
const immutable_1 = require("immutable");
const colorjs_io_1 = require("colorjs.io");
/** Legacy determination of color space by channel name. */
function getColorSpace(options) {
    if (typeof options.red === 'number')
        return 'rgb';
    if (typeof options.saturation === 'number')
        return 'hsl';
    if (typeof options.whiteness === 'number')
        return 'hwb';
    throw (0, utils_1.valueError)('No color space found');
}
/**
 * Convert from the ColorJS representation of a missing component (`NaN`) to
 * `null`.
 */
function NaNtoNull(val) {
    return Number.isNaN(val) ? null : val;
}
/**
 * Convert from the ColorJS representation of a missing component (`NaN`) to
 * `0`.
 */
function NaNtoZero(val) {
    return Number.isNaN(val) ? 0 : val;
}
/** Convert from sRGB (0-1) to RGB (0-255) units. */
function coordToRgb(val) {
    return val * 255;
}
/** Normalize `hue` values to be within the range `[0, 360)`. */
function normalizeHue(val) {
    return (0, utils_2.positiveMod)(val, 360);
}
/**
 * Normalize discrepancies between Sass color spaces and ColorJS color space
 * ids, converting Sass values to ColorJS values.
 */
function encodeSpaceForColorJs(space) {
    switch (space) {
        case 'rgb':
            return 'srgb';
        case 'a98-rgb':
            return 'a98rgb';
        case 'display-p3':
            return 'p3';
        case 'prophoto-rgb':
            return 'prophoto';
    }
    return space;
}
/**
 * Normalize discrepancies between Sass's [GamutMapMethod] and Color.js's
 * `method` option.
 */
function encodeGamutMapMethodForColorJs(method) {
    return method === 'local-minde' ? 'css' : method;
}
/**
 * Normalize discrepancies between Sass color spaces and ColorJS color space
 * ids, converting ColorJS values to Sass values.
 */
function decodeSpaceFromColorJs(space, isRgb = false) {
    switch (space) {
        case 'srgb':
            return isRgb ? 'rgb' : space;
        case 'xyz-d65':
            return 'xyz';
        case 'a98rgb':
            return 'a98-rgb';
        case 'p3':
            return 'display-p3';
        case 'prophoto':
            return 'prophoto-rgb';
    }
    return space;
}
/**
 * Normalize discrepancies between Sass channel names and ColorJS channel ids,
 * converting Sass values to ColorJS values.
 *
 * @TODO Waiting on a new release of ColorJS that allows Lab spaces to accept
 * `lightness` instead of only `l` and not as a channel name.
 * Fixed in: https://github.com/LeaVerou/color.js/pull/348
 */
function encodeChannelForColorJs(channel) {
    if (channel === 'lightness')
        return 'l';
    return channel;
}
/**
 * Implement our own check of channel name validity for a given space, because
 * ColorJS allows e.g. `b` for any of `blue`, `blackness`, or `b` channels.
 */
function validateChannelInSpace(channel, space) {
    if (channel === 'alpha')
        return;
    let valid = false;
    switch (space) {
        case 'rgb':
        case 'srgb':
        case 'srgb-linear':
        case 'display-p3':
        case 'a98-rgb':
        case 'prophoto-rgb':
        case 'rec2020':
            valid = ['red', 'green', 'blue'].includes(channel);
            break;
        case 'hsl':
            valid = ['hue', 'saturation', 'lightness'].includes(channel);
            break;
        case 'hwb':
            valid = ['hue', 'whiteness', 'blackness'].includes(channel);
            break;
        case 'lab':
        case 'oklab':
            valid = ['lightness', 'a', 'b'].includes(channel);
            break;
        case 'lch':
        case 'oklch':
            valid = ['lightness', 'chroma', 'hue'].includes(channel);
            break;
        case 'xyz':
        case 'xyz-d65':
        case 'xyz-d50':
            valid = ['x', 'y', 'z'].includes(channel);
            break;
    }
    if (!valid) {
        throw (0, utils_1.valueError)(`Unknown channel name "${channel}" for color space "${space}".`);
    }
}
/** Determine whether the given space is a polar color space. */
function isPolarColorSpace(space) {
    switch (space) {
        case 'hsl':
        case 'hwb':
        case 'lch':
        case 'oklch':
            return true;
        default:
            return false;
    }
}
/**
 * Convert from ColorJS coordinates (which use `NaN` for missing components, and
 * a range of `0-1` for `rgb` channel values) to Sass Color coordinates (which
 * use `null` for missing components, and a range of `0-255` for `rgb` channel
 * values).
 */
function decodeCoordsFromColorJs(coords, // ColorJS coordinates
isRgb = false) {
    let newCoords = coords;
    // If this color is in the `rgb` space, convert channel values to `0-255`
    if (isRgb)
        newCoords = newCoords.map(coordToRgb);
    // Convert `NaN` values to `null`
    return newCoords.map(NaNtoNull);
}
/** Returns `true` if `val` is a `number` or `null`. */
function isNumberOrNull(val) {
    return val === null || typeof val === 'number';
}
/**
 * Emit deprecation warnings when legacy color spaces set `alpha` or channel
 * values to `null` without explicitly setting the `space`.
 */
function checkChangeDeprecations(options, channels) {
    if (options.alpha === null)
        emitNullAlphaDeprecation();
    for (const channel of channels) {
        if (options[channel] === null)
            emitColor4ApiChangeNullDeprecation(channel);
    }
}
/** Warn users about legacy color channel getters. */
function emitColor4ApiGetterDeprecation(name) {
    (0, deprecations_1.warnForHostSideDeprecation)(`\`${name}\` is deprecated, use \`channel\` instead.` +
        '\n' +
        'More info: https://sass-lang.com/d/color-4-api', deprecations_1.deprecations['color-4-api']);
}
/**
 * Warn users about changing channels not in the current color space without
 * explicitly setting `space`.
 */
function emitColor4ApiChangeSpaceDeprecation() {
    (0, deprecations_1.warnForHostSideDeprecation)("Changing a channel not in this color's space without explicitly " +
        'specifying the `space` option is deprecated.' +
        '\n' +
        'More info: https://sass-lang.com/d/color-4-api', deprecations_1.deprecations['color-4-api']);
}
/** Warn users about `null` channel values without setting `space`. */
function emitColor4ApiChangeNullDeprecation(channel) {
    (0, deprecations_1.warnForHostSideDeprecation)(`Passing \`${channel}: null\` without setting \`space\` is deprecated.` +
        '\n' +
        'More info: https://sass-lang.com/d/color-4-api', deprecations_1.deprecations['color-4-api']);
}
/** Warn users about null-alpha deprecation. */
function emitNullAlphaDeprecation() {
    (0, deprecations_1.warnForHostSideDeprecation)('Passing `alpha: null` without setting `space` is deprecated.' +
        '\n' +
        'More info: https://sass-lang.com/d/null-alpha', deprecations_1.deprecations['null-alpha']);
}
/**
 * Determines whether the options passed to the Constructor include an existing
 * ColorJS color object.
 */
function optionsHaveColor(opts) {
    return opts.color instanceof colorjs_io_1.default;
}
/** A SassScript color. */
class SassColor extends index_1.Value {
    // ColorJS color object
    color;
    // Boolean indicating whether this color is in RGB format
    //
    // ColorJS treats `rgb` as an output format of the `srgb` color space, while
    // Sass treats it as its own color space. By internally tracking whether this
    // color is `rgb` or not, we can use `srgb` consistently for ColorJS while
    // still returning expected `rgb` values for Sass users.
    isRgb = false;
    // Names for the channels of this color
    channel0Id;
    channel1Id;
    channel2Id;
    // Sets channel names based on this color's color space
    setChannelIds(space) {
        switch (space) {
            case 'rgb':
            case 'srgb':
            case 'srgb-linear':
            case 'display-p3':
            case 'a98-rgb':
            case 'prophoto-rgb':
            case 'rec2020':
                this.channel0Id = 'red';
                this.channel1Id = 'green';
                this.channel2Id = 'blue';
                break;
            case 'hsl':
                this.channel0Id = 'hue';
                this.channel1Id = 'saturation';
                this.channel2Id = 'lightness';
                break;
            case 'hwb':
                this.channel0Id = 'hue';
                this.channel1Id = 'whiteness';
                this.channel2Id = 'blackness';
                break;
            case 'lab':
            case 'oklab':
                this.channel0Id = 'lightness';
                this.channel1Id = 'a';
                this.channel2Id = 'b';
                break;
            case 'lch':
            case 'oklch':
                this.channel0Id = 'lightness';
                this.channel1Id = 'chroma';
                this.channel2Id = 'hue';
                break;
            case 'xyz':
            case 'xyz-d65':
            case 'xyz-d50':
                this.channel0Id = 'x';
                this.channel1Id = 'y';
                this.channel2Id = 'z';
                break;
        }
    }
    constructor(optionsMaybeWithColor) {
        super();
        let options;
        // Use existing ColorJS color object from options for the new SassColor
        if (optionsHaveColor(optionsMaybeWithColor)) {
            const { color, space } = optionsMaybeWithColor;
            if (space === 'rgb')
                this.isRgb = true;
            this.setChannelIds(space);
            this.color = color;
            return;
        }
        else {
            options = optionsMaybeWithColor;
        }
        const space = options.space ?? getColorSpace(options);
        this.setChannelIds(space);
        if (space === 'rgb')
            this.isRgb = true;
        let alpha;
        if (options.alpha === null) {
            if (!options.space)
                emitNullAlphaDeprecation();
            alpha = NaN;
        }
        else if (options.alpha === undefined) {
            alpha = 1;
        }
        else {
            alpha = (0, utils_2.fuzzyAssertInRange)(options.alpha, 0, 1, 'alpha');
        }
        switch (space) {
            case 'rgb':
            case 'srgb': {
                const red = options.red ?? NaN;
                const green = options.green ?? NaN;
                const blue = options.blue ?? NaN;
                if (this.isRgb) {
                    this.color = new colorjs_io_1.default({
                        spaceId: encodeSpaceForColorJs(space),
                        // convert from 0-255 to 0-1
                        coords: [red / 255, green / 255, blue / 255],
                        alpha,
                    });
                }
                else {
                    this.color = new colorjs_io_1.default({
                        spaceId: encodeSpaceForColorJs(space),
                        coords: [red, green, blue],
                        alpha,
                    });
                }
                break;
            }
            case 'srgb-linear':
            case 'display-p3':
            case 'a98-rgb':
            case 'prophoto-rgb':
            case 'rec2020':
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [
                        options.red ?? NaN,
                        options.green ?? NaN,
                        options.blue ?? NaN,
                    ],
                    alpha,
                });
                break;
            case 'hsl': {
                let hue = normalizeHue(options.hue ?? NaN);
                let saturation = options.saturation ?? NaN;
                const lightness = options.lightness ?? NaN;
                if (!Number.isNaN(saturation) && (0, utils_2.fuzzyLessThan)(saturation, 0)) {
                    saturation = Math.abs(saturation);
                    hue = (hue + 180) % 360;
                }
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [hue, saturation, lightness],
                    alpha,
                });
                break;
            }
            case 'hwb': {
                const hue = normalizeHue(options.hue ?? NaN);
                const whiteness = options.whiteness ?? NaN;
                const blackness = options.blackness ?? NaN;
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [hue, whiteness, blackness],
                    alpha,
                });
                break;
            }
            case 'lab':
            case 'oklab': {
                const lightness = options.lightness ?? NaN;
                const a = options.a ?? NaN;
                const b = options.b ?? NaN;
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [lightness, a, b],
                    alpha,
                });
                break;
            }
            case 'lch':
            case 'oklch': {
                const lightness = options.lightness ?? NaN;
                let chroma = options.chroma ?? NaN;
                let hue = normalizeHue(options.hue ?? NaN);
                if (!Number.isNaN(chroma) && (0, utils_2.fuzzyLessThan)(chroma, 0)) {
                    chroma = Math.abs(chroma);
                    hue = (hue + 180) % 360;
                }
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [lightness, chroma, hue],
                    alpha,
                });
                break;
            }
            case 'xyz':
            case 'xyz-d65':
            case 'xyz-d50':
                this.color = new colorjs_io_1.default({
                    spaceId: encodeSpaceForColorJs(space),
                    coords: [options.x ?? NaN, options.y ?? NaN, options.z ?? NaN],
                    alpha,
                });
                break;
        }
        // @TODO Waiting on new release of ColorJS that includes allowing `alpha`
        // to be `NaN` on initial construction.
        // Fixed in: https://github.com/LeaVerou/color.js/commit/08b39c180565ae61408ad737d91bd71a1f79d3df
        if (Number.isNaN(alpha)) {
            this.color.alpha = NaN;
        }
    }
    /** This color's alpha channel, between `0` and `1`. */
    get alpha() {
        return NaNtoZero(this.color.alpha);
    }
    /** The name of this color's color space. */
    get space() {
        return decodeSpaceFromColorJs(this.color.spaceId, this.isRgb);
    }
    /**
     * A boolean indicating whether this color is in a legacy color space (`rgb`,
     * `hsl`, or `hwb`).
     */
    get isLegacy() {
        return ['rgb', 'hsl', 'hwb'].includes(this.space);
    }
    /**
     * A list of this color's channel values (excluding alpha), with [missing
     * channels] converted to `null`.
     *
     * [missing channels]: https://developer.mozilla.org/en-US/docs/Web/CSS/color_value#missing_color_components
     */
    get channelsOrNull() {
        let coords = this.color.coords;
        if (this.space === 'rgb') {
            coords = coords.map(coordToRgb);
        }
        return (0, immutable_1.List)(coords.map(NaNtoNull));
    }
    /**
     * A list of this color's channel values (excluding alpha), with [missing
     * channels] converted to `0`.
     *
     * [missing channels]: https://developer.mozilla.org/en-US/docs/Web/CSS/color_value#missing_color_components
     */
    get channels() {
        let coords = this.color.coords;
        if (this.space === 'rgb') {
            coords = coords.map(coordToRgb);
        }
        return (0, immutable_1.List)(coords.map(NaNtoZero));
    }
    /**
     * This color's red channel in the RGB color space, between `0` and `255`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get red() {
        emitColor4ApiGetterDeprecation('red');
        const val = NaNtoZero(coordToRgb(this.color.srgb.red));
        return (0, utils_2.fuzzyRound)(val);
    }
    /**
     * This color's green channel in the RGB color space, between `0` and `255`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get green() {
        emitColor4ApiGetterDeprecation('green');
        const val = NaNtoZero(coordToRgb(this.color.srgb.green));
        return (0, utils_2.fuzzyRound)(val);
    }
    /**
     * This color's blue channel in the RGB color space, between `0` and `255`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get blue() {
        emitColor4ApiGetterDeprecation('blue');
        const val = NaNtoZero(coordToRgb(this.color.srgb.blue));
        return (0, utils_2.fuzzyRound)(val);
    }
    /**
     * This color's hue in the HSL color space, between `0` and `360`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get hue() {
        emitColor4ApiGetterDeprecation('hue');
        return NaNtoZero(this.color.hsl.hue);
    }
    /**
     * This color's saturation in the HSL color space, between `0` and `100`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get saturation() {
        emitColor4ApiGetterDeprecation('saturation');
        return NaNtoZero(this.color.hsl.saturation);
    }
    /**
     * This color's lightness in the HSL color space, between `0` and `100`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get lightness() {
        emitColor4ApiGetterDeprecation('lightness');
        return NaNtoZero(this.color.hsl.lightness);
    }
    /**
     * This color's whiteness in the HWB color space, between `0` and `100`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get whiteness() {
        emitColor4ApiGetterDeprecation('whiteness');
        return NaNtoZero(this.color.hwb.whiteness);
    }
    /**
     * This color's blackness in the HWB color space, between `0` and `100`.
     *
     * @deprecated Use {@link channel} instead.
     */
    get blackness() {
        emitColor4ApiGetterDeprecation('blackness');
        return NaNtoZero(this.color.hwb.blackness);
    }
    assertColor() {
        return this;
    }
    /**
     * Returns a new color that's the result of converting this color to the
     * specified `space`.
     */
    toSpace(space) {
        if (space === this.space)
            return this;
        const color = this.color.to(encodeSpaceForColorJs(space));
        return new SassColor({ color, space });
    }
    /**
     * Returns a boolean indicating whether this color is in-gamut (as opposed to
     * having one or more of its channels out of bounds) for the specified
     * `space`, or its current color space if `space` is not specified.
     */
    isInGamut(space) {
        return this.color.inGamut(encodeSpaceForColorJs(space));
    }
    /**
     * Returns a copy of this color, modified so it is in-gamut for the specified
     * `space`—or the current color space if `space` is not specified—using
     * `method` to map out-of-gamut colors into the desired gamut.
     */
    toGamut({ space, method, }) {
        if (this.isInGamut(space))
            return this;
        const color = this.color.clone().toGamut({
            space: encodeSpaceForColorJs(space),
            method: encodeGamutMapMethodForColorJs(method),
        });
        return new SassColor({ color, space: space ?? this.space });
    }
    channel(channel, options) {
        if (channel === 'alpha')
            return this.alpha;
        let val;
        const space = options?.space ?? this.space;
        validateChannelInSpace(channel, space);
        if (options?.space) {
            val = this.color.get({
                space: encodeSpaceForColorJs(options.space),
                coordId: encodeChannelForColorJs(channel),
            });
        }
        else {
            val = this.color.get({
                space: this.color.spaceId,
                coordId: encodeChannelForColorJs(channel),
            });
        }
        if (space === 'rgb')
            val = coordToRgb(val);
        return NaNtoZero(val);
    }
    /**
     * Returns a boolean indicating whether a given channel value is a [missing
     * channel].
     *
     * [missing channel]: https://developer.mozilla.org/en-US/docs/Web/CSS/color_value#missing_color_components
     */
    isChannelMissing(channel) {
        if (channel === 'alpha')
            return Number.isNaN(this.color.alpha);
        validateChannelInSpace(channel, this.space);
        return Number.isNaN(this.color.get({
            space: this.color.spaceId,
            coordId: encodeChannelForColorJs(channel),
        }));
    }
    isChannelPowerless(channel, options) {
        if (channel === 'alpha')
            return false;
        const color = options?.space ? this.toSpace(options.space) : this;
        validateChannelInSpace(channel, color.space);
        const channels = color.channels.toArray();
        switch (channel) {
            case color.channel0Id:
                if (color.space === 'hsl')
                    return (0, utils_2.fuzzyEquals)(channels[1], 0);
                if (color.space === 'hwb') {
                    return (0, utils_2.fuzzyGreaterThanOrEquals)(channels[1] + channels[2], 100);
                }
                return false;
            case color.channel2Id:
                switch (color.space) {
                    case 'lch':
                    case 'oklch':
                        return (0, utils_2.fuzzyEquals)(channels[1], 0);
                }
                return false;
        }
        return false;
    }
    /**
     * Returns a color partway between this color and `color2` according to
     * `method`, as defined by the CSS Color 4 [color interpolation] procedure.
     *
     * [color interpolation]: https://www.w3.org/TR/css-color-4/#interpolation
     *
     * If `method` is missing and this color is in a polar color space (HSL, HWB,
     * LCH, and Oklch spaces), `method` defaults to "shorter".
     *
     * The `weight` is a number between 0 and 1 that indicates how much of this
     * color should be in the resulting color. If omitted, it defaults to 0.5.
     */
    interpolate(color2, options) {
        const hueInterpolationMethod = options?.method ??
            (isPolarColorSpace(this.space) ? 'shorter' : undefined);
        const weight = options?.weight ?? 0.5;
        if ((0, utils_2.fuzzyEquals)(weight, 0))
            return color2;
        if ((0, utils_2.fuzzyEquals)(weight, 1))
            return this;
        if (weight < 0 || weight > 1) {
            throw (0, utils_1.valueError)(`Expected \`weight\` between \`0\` and \`1\`, received \`${weight}\`.`);
        }
        // ColorJS inverses the `weight` argument, where `0` is `this` and `1` is
        // `color2`.
        const color = this.color.mix(color2.color, 1 - weight, {
            space: encodeSpaceForColorJs(this.space),
            hue: hueInterpolationMethod,
        });
        const coords = decodeCoordsFromColorJs(color.coords, this.space === 'rgb');
        return new SassColor({
            space: this.space,
            [this.channel0Id]: coords[0],
            [this.channel1Id]: coords[1],
            [this.channel2Id]: coords[2],
            alpha: NaNtoNull(this.color.alpha),
        });
    }
    /** Legacy determination of color space by option channels. */
    getLegacyChangeSpace(options) {
        let space;
        if (isNumberOrNull(options.whiteness) ||
            isNumberOrNull(options.blackness) ||
            (this.space === 'hwb' && isNumberOrNull(options.hue))) {
            space = 'hwb';
        }
        else if (isNumberOrNull(options.hue) ||
            isNumberOrNull(options.saturation) ||
            isNumberOrNull(options.lightness)) {
            space = 'hsl';
        }
        else if (isNumberOrNull(options.red) ||
            isNumberOrNull(options.green) ||
            isNumberOrNull(options.blue)) {
            space = 'rgb';
        }
        if (space !== this.space)
            emitColor4ApiChangeSpaceDeprecation();
        return space ?? this.space;
    }
    /**
     * Returns a new SassColor in the given `space` that's the result of changing
     * one or more of this color's channels.
     */
    getChangedColor(options, space, spaceSetExplicitly) {
        const color = this.toSpace(space);
        function getChangedValue(channel) {
            if (isNumberOrNull(options[channel]))
                return options[channel];
            return color.channel(channel);
        }
        switch (space) {
            case 'hsl':
                if (spaceSetExplicitly) {
                    return new SassColor({
                        hue: getChangedValue('hue'),
                        saturation: getChangedValue('saturation'),
                        lightness: getChangedValue('lightness'),
                        alpha: getChangedValue('alpha'),
                        space,
                    });
                }
                else {
                    checkChangeDeprecations(options, ['hue', 'saturation', 'lightness']);
                    return new SassColor({
                        hue: options.hue ?? color.channel('hue'),
                        saturation: options.saturation ?? color.channel('saturation'),
                        lightness: options.lightness ?? color.channel('lightness'),
                        alpha: options.alpha ?? color.channel('alpha'),
                        space,
                    });
                }
            case 'hwb':
                if (spaceSetExplicitly) {
                    return new SassColor({
                        hue: getChangedValue('hue'),
                        whiteness: getChangedValue('whiteness'),
                        blackness: getChangedValue('blackness'),
                        alpha: getChangedValue('alpha'),
                        space,
                    });
                }
                else {
                    checkChangeDeprecations(options, ['hue', 'whiteness', 'blackness']);
                    return new SassColor({
                        hue: options.hue ?? color.channel('hue'),
                        whiteness: options.whiteness ?? color.channel('whiteness'),
                        blackness: options.blackness ?? color.channel('blackness'),
                        alpha: options.alpha ?? color.channel('alpha'),
                        space,
                    });
                }
            case 'rgb':
                if (spaceSetExplicitly) {
                    return new SassColor({
                        red: getChangedValue('red'),
                        green: getChangedValue('green'),
                        blue: getChangedValue('blue'),
                        alpha: getChangedValue('alpha'),
                        space,
                    });
                }
                else {
                    checkChangeDeprecations(options, ['red', 'green', 'blue']);
                    return new SassColor({
                        red: options.red ?? color.channel('red'),
                        green: options.green ?? color.channel('green'),
                        blue: options.blue ?? color.channel('blue'),
                        alpha: options.alpha ?? color.channel('alpha'),
                        space,
                    });
                }
            case 'lab':
            case 'oklab':
                return new SassColor({
                    lightness: getChangedValue('lightness'),
                    a: getChangedValue('a'),
                    b: getChangedValue('b'),
                    alpha: getChangedValue('alpha'),
                    space,
                });
            case 'lch':
            case 'oklch':
                return new SassColor({
                    lightness: getChangedValue('lightness'),
                    chroma: getChangedValue('chroma'),
                    hue: getChangedValue('hue'),
                    alpha: getChangedValue('alpha'),
                    space,
                });
            case 'a98-rgb':
            case 'display-p3':
            case 'prophoto-rgb':
            case 'rec2020':
            case 'srgb':
            case 'srgb-linear':
                return new SassColor({
                    red: getChangedValue('red'),
                    green: getChangedValue('green'),
                    blue: getChangedValue('blue'),
                    alpha: getChangedValue('alpha'),
                    space,
                });
            case 'xyz':
            case 'xyz-d50':
            case 'xyz-d65':
                return new SassColor({
                    y: getChangedValue('y'),
                    x: getChangedValue('x'),
                    z: getChangedValue('z'),
                    alpha: getChangedValue('alpha'),
                    space,
                });
        }
    }
    change(options) {
        const spaceSetExplicitly = !!options.space;
        let space = options.space ?? this.space;
        if (this.isLegacy && !spaceSetExplicitly) {
            space = this.getLegacyChangeSpace(options);
        }
        // Validate channel values
        const keys = Object.keys(options).filter(key => key !== 'space');
        for (const channel of keys) {
            validateChannelInSpace(channel, space);
        }
        if (isNumberOrNull(options.alpha) && options.alpha !== null) {
            (0, utils_2.fuzzyAssertInRange)(options.alpha, 0, 1, 'alpha');
        }
        return this.getChangedColor(options, space, spaceSetExplicitly).toSpace(this.space);
    }
    equals(other) {
        if (!(other instanceof SassColor))
            return false;
        let coords = this.color.coords;
        let otherCoords = other.color.coords;
        if (this.isLegacy) {
            if (!other.isLegacy)
                return false;
            if (!(0, utils_2.fuzzyEquals)(this.alpha, other.alpha))
                return false;
            if (!(this.space === 'rgb' && other.space === 'rgb')) {
                coords = this.color
                    .to('srgb')
                    .coords.map(coordToRgb)
                    .map(utils_2.fuzzyRound);
                otherCoords = other.color
                    .to('srgb')
                    .coords.map(coordToRgb)
                    .map(utils_2.fuzzyRound);
            }
            return ((0, utils_2.fuzzyEquals)(coords[0], otherCoords[0]) &&
                (0, utils_2.fuzzyEquals)(coords[1], otherCoords[1]) &&
                (0, utils_2.fuzzyEquals)(coords[2], otherCoords[2]));
        }
        return (this.space === other.space &&
            (0, utils_2.fuzzyEquals)(coords[0], otherCoords[0]) &&
            (0, utils_2.fuzzyEquals)(coords[1], otherCoords[1]) &&
            (0, utils_2.fuzzyEquals)(coords[2], otherCoords[2]) &&
            (0, utils_2.fuzzyEquals)(this.alpha, other.alpha));
    }
    hashCode() {
        let coords = this.color.coords;
        if (this.isLegacy) {
            coords = this.color.to('srgb').coords.map(coordToRgb).map(utils_2.fuzzyRound);
            return ((0, utils_2.fuzzyHashCode)(coords[0]) ^
                (0, utils_2.fuzzyHashCode)(coords[1]) ^
                (0, utils_2.fuzzyHashCode)(coords[2]) ^
                (0, utils_2.fuzzyHashCode)(this.alpha));
        }
        return ((0, immutable_1.hash)(this.space) ^
            (0, utils_2.fuzzyHashCode)(coords[0]) ^
            (0, utils_2.fuzzyHashCode)(coords[1]) ^
            (0, utils_2.fuzzyHashCode)(coords[2]) ^
            (0, utils_2.fuzzyHashCode)(this.alpha));
    }
    toString() {
        return this.color.toString({ inGamut: false });
    }
}
exports.SassColor = SassColor;
//# sourceMappingURL=color.js.map