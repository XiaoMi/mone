"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.thenOr = thenOr;
exports.catchOr = catchOr;
exports.isNullOrUndefined = isNullOrUndefined;
exports.asImmutableList = asImmutableList;
exports.compilerError = compilerError;
exports.mandatoryError = mandatoryError;
exports.hostError = hostError;
exports.valueError = valueError;
exports.pathToUrlString = pathToUrlString;
exports.fileUrlToPathCrossPlatform = fileUrlToPathCrossPlatform;
exports.withoutExtension = withoutExtension;
exports.protofySyntax = protofySyntax;
exports.isErrnoException = isErrnoException;
exports.putIfAbsent = putIfAbsent;
const immutable_1 = require("immutable");
const p = require("path");
const url = require("url");
const proto = require("./vendor/embedded_sass_pb");
/**
 * The equivalent of `Promise.then()`, except that if the first argument is a
 * plain value it synchronously invokes `callback()` and returns its result.
 */
function thenOr(promiseOrValue, callback) {
    return promiseOrValue instanceof Promise
        ? promiseOrValue.then(callback)
        : callback(promiseOrValue);
}
/**
 * The equivalent of `Promise.catch()`, except that if the first argument throws
 * synchronously it synchronously invokes `callback()` and returns its result.
 */
function catchOr(promiseOrValueCallback, callback) {
    try {
        const result = promiseOrValueCallback();
        return result instanceof Promise
            ? result.catch(callback)
            : result;
    }
    catch (error) {
        return callback(error);
    }
}
/** Checks for null or undefined. */
function isNullOrUndefined(object) {
    return object === null || object === undefined;
}
/** Returns `collection` as an immutable List. */
function asImmutableList(collection) {
    return immutable_1.List.isList(collection) ? collection : (0, immutable_1.List)(collection);
}
/** Constructs a compiler-caused Error. */
function compilerError(message) {
    return Error(`Compiler caused error: ${message}.`);
}
/**
 * Returns a `compilerError()` indicating that the given `field` should have
 * been included but was not.
 */
function mandatoryError(field) {
    return compilerError(`Missing mandatory field ${field}`);
}
/** Constructs a host-caused Error. */
function hostError(message) {
    return Error(`Compiler reported error: ${message}.`);
}
/** Constructs an error caused by an invalid value type. */
function valueError(message, name) {
    return Error(name ? `$${name}: ${message}.` : `${message}.`);
}
// Node changed its implementation of pathToFileURL:
// https://github.com/nodejs/node/pull/54545
const unsafePathToFileURL = url.pathToFileURL('~').pathname.endsWith('~');
/** Converts a (possibly relative) path on the local filesystem to a URL. */
function pathToUrlString(path) {
    if (p.isAbsolute(path))
        return url.pathToFileURL(path).toString();
    // percent encode relative path like `pathToFileURL`
    let fileUrl = encodeURI(path).replace(/[#?]/g, encodeURIComponent);
    if (unsafePathToFileURL) {
        fileUrl = fileUrl.replace(/%(5B|5D|5E|7C)/g, decodeURIComponent);
    }
    else {
        fileUrl = fileUrl.replace(/~/g, '%7E');
    }
    if (process.platform === 'win32') {
        fileUrl = fileUrl.replace(/%5C/g, '/');
    }
    return fileUrl;
}
/**
 * Like `url.fileURLToPath`, but returns the same result for Windows-style file
 * URLs on all platforms.
 */
function fileUrlToPathCrossPlatform(fileUrl) {
    const path = url.fileURLToPath(fileUrl);
    // Windows file: URLs begin with `file:///C:/` (or another drive letter),
    // which `fileURLToPath` converts to `"/C:/"` on non-Windows systems. We want
    // to ensure the behavior is consistent across OSes, so we normalize this back
    // to a Windows-style path.
    return /^\/[A-Za-z]:\//.test(path) ? path.substring(1) : path;
}
/** Returns `path` without an extension, if it had one. */
function withoutExtension(path) {
    const extension = p.extname(path);
    return path.substring(0, path.length - extension.length);
}
/** Converts a JS syntax string into a protobuf syntax enum. */
function protofySyntax(syntax) {
    switch (syntax) {
        case 'scss':
            return proto.Syntax.SCSS;
        case 'indented':
            return proto.Syntax.INDENTED;
        case 'css':
            return proto.Syntax.CSS;
        default:
            throw new Error(`Unknown syntax: "${syntax}"`);
    }
}
/** Returns whether `error` is a NodeJS-style exception with an error code. */
function isErrnoException(error) {
    return error instanceof Error && ('errno' in error || 'code' in error);
}
/**
 * Dart-style utility. See
 * http://go/dart-api/stable/2.8.4/dart-core/Map/putIfAbsent.html.
 */
function putIfAbsent(map, key, provider) {
    const val = map.get(key);
    if (val !== undefined) {
        return val;
    }
    else {
        const newVal = provider();
        map.set(key, newVal);
        return newVal;
    }
}
//# sourceMappingURL=utils.js.map