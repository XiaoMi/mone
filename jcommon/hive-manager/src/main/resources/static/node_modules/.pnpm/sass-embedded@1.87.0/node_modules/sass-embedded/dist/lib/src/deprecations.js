"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.activeDeprecationOptions = exports.deprecations = void 0;
exports.getDeprecationIds = getDeprecationIds;
exports.warnForHostSideDeprecation = warnForHostSideDeprecation;
const version_1 = require("./version");
var deprecations_1 = require("./vendor/deprecations");
Object.defineProperty(exports, "deprecations", { enumerable: true, get: function () { return deprecations_1.deprecations; } });
/**
 * Converts a mixed array of deprecations, IDs, and versions to an array of IDs
 * that's ready to include in a CompileRequest.
 */
function getDeprecationIds(arr) {
    return arr.map(item => {
        if (item instanceof version_1.Version) {
            return item.toString();
        }
        else if (typeof item === 'string') {
            return item;
        }
        return item.id;
    });
}
/**
 * Map between active compilations and the deprecation options they use.
 *
 * This is used to determine which options to use when handling host-side
 * deprecation warnings that aren't explicitly tied to a particular compilation.
 */
exports.activeDeprecationOptions = new Map();
/**
 * Handles a host-side deprecation warning, either emitting a warning, throwing
 * an error, or doing nothing depending on the deprecation options used.
 *
 * If no specific deprecation options are passed here, then options will be
 * determined based on the options of the active compilations.
 */
function warnForHostSideDeprecation(message, deprecation, options) {
    if (deprecation.status === 'future' &&
        !isEnabledFuture(deprecation, options)) {
        return;
    }
    const fullMessage = `Deprecation [${deprecation.id}]: ${message}`;
    if (isFatal(deprecation, options)) {
        throw Error(fullMessage);
    }
    if (!isSilent(deprecation, options)) {
        console.warn(fullMessage);
    }
}
/**
 * Checks whether the given deprecation is included in the given list of silent
 * deprecations or is silenced by at least one active compilation.
 */
function isSilent(deprecation, options) {
    if (!options) {
        for (const potentialOptions of exports.activeDeprecationOptions.values()) {
            if (isSilent(deprecation, potentialOptions))
                return true;
        }
        return false;
    }
    return getDeprecationIds(options.silenceDeprecations ?? []).includes(deprecation.id);
}
/**
 * Checks whether the given deprecation is included in the given list of future
 * deprecations that should be enabled or is enabled in all active compilations.
 */
function isEnabledFuture(deprecation, options) {
    if (!options) {
        for (const potentialOptions of exports.activeDeprecationOptions.values()) {
            if (!isEnabledFuture(deprecation, potentialOptions))
                return false;
        }
        return exports.activeDeprecationOptions.size > 0;
    }
    return getDeprecationIds(options.futureDeprecations ?? []).includes(deprecation.id);
}
/**
 * Checks whether the given deprecation is included in the given list of
 * fatal deprecations or is marked as fatal in all active compilations.
 */
function isFatal(deprecation, options) {
    if (!options) {
        for (const potentialOptions of exports.activeDeprecationOptions.values()) {
            if (!isFatal(deprecation, potentialOptions))
                return false;
        }
        return exports.activeDeprecationOptions.size > 0;
    }
    const versionNumber = deprecation.deprecatedIn === null
        ? null
        : deprecation.deprecatedIn.major * 1000000 +
            deprecation.deprecatedIn.minor * 1000 +
            deprecation.deprecatedIn.patch;
    for (const fatal of options.fatalDeprecations ?? []) {
        if (fatal instanceof version_1.Version) {
            if (versionNumber === null)
                continue;
            if (versionNumber <=
                fatal.major * 1000000 + fatal.minor * 1000 + fatal.patch) {
                return true;
            }
        }
        else if (typeof fatal === 'string') {
            if (fatal === deprecation.id)
                return true;
        }
        else {
            if (fatal.id === deprecation.id)
                return true;
        }
    }
    return false;
}
//# sourceMappingURL=deprecations.js.map