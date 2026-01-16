"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyImporterWrapper = exports.metaNamespace = exports.legacyImporterFileProtocol = exports.endOfLoadProtocol = void 0;
const assert_1 = require("assert");
const fs = require("fs");
const p = require("path");
const util = require("util");
const resolve_path_1 = require("./resolve-path");
const utils_1 = require("../utils");
const utils_2 = require("./utils");
/**
 * A special URL protocol we use to signal when a stylesheet has finished
 * loading. This allows us to determine which stylesheet is "current" when
 * resolving a new load, which in turn allows us to pass in an accurate `prev`
 * parameter to the legacy callback.
 */
exports.endOfLoadProtocol = 'sass-embedded-legacy-load-done:';
/**
 * The `file:` URL protocol with [legacyImporterProtocolPrefix] at the beginning.
 */
exports.legacyImporterFileProtocol = 'legacy-importer-file:';
/**
 * A random namespace for `sass:meta`, so we can use `meta.load-css()` at the end
 * of the file to signal that a load has finished without polluting a namespace
 * a user might actually use.
 */
exports.metaNamespace = `---${Math.random().toString(36).substring(2)}`;
// A count of `endOfLoadProtocol` imports that have been generated. Each one
// must be a different URL to ensure that the importer results aren't cached.
let endOfLoadCount = 0;
/**
 * A wrapper around a `LegacyImporter` callback that exposes it as a new-API
 * `Importer`.
 */
class LegacyImporterWrapper {
    self;
    callbacks;
    loadPaths;
    sync;
    // A stack of previous URLs passed to `this.callbacks`.
    prev = [];
    // The `contents` field returned by the last successful invocation of
    // `this.callbacks`, if it returned one.
    lastContents;
    constructor(self, callbacks, loadPaths, initialPrev, sync) {
        this.self = self;
        this.callbacks = callbacks;
        this.loadPaths = loadPaths;
        this.sync = sync;
        const path = initialPrev !== 'stdin';
        this.prev.push({ url: path ? p.resolve(initialPrev) : 'stdin', path });
    }
    canonicalize(url, options) {
        if (url.startsWith(exports.endOfLoadProtocol))
            return new URL(url);
        // Emulate a base importer instead of using a real base importer,
        // because we want to mark containingUrl as used, which is impossible
        // in a real base importer.
        if (options.containingUrl !== null) {
            try {
                const absoluteUrl = new URL(url, options.containingUrl).toString();
                const resolved = this.canonicalize(absoluteUrl, {
                    fromImport: options.fromImport,
                    containingUrl: null,
                });
                if (resolved !== null)
                    return resolved;
            }
            catch (error) {
                if (error instanceof TypeError &&
                    (0, utils_1.isErrnoException)(error) &&
                    error.code === 'ERR_INVALID_URL') {
                    // ignore
                }
                else {
                    throw error;
                }
            }
        }
        if (url.startsWith(utils_2.legacyImporterProtocolPrefix) ||
            url.startsWith(utils_2.legacyImporterProtocol)) {
            // A load starts with `legacyImporterProtocolPrefix` if and only if it's a
            // relative load for the current importer rather than an absolute load.
            // For the most part, we want to ignore these, but for `file:` URLs
            // specifically we want to resolve them on the filesystem to ensure
            // locality.
            const urlWithoutPrefix = url.substring(utils_2.legacyImporterProtocolPrefix.length);
            if (urlWithoutPrefix.startsWith('file:')) {
                let resolved = null;
                try {
                    const path = (0, utils_1.fileUrlToPathCrossPlatform)(urlWithoutPrefix);
                    resolved = (0, resolve_path_1.resolvePath)(path, options.fromImport);
                }
                catch (error) {
                    if (error instanceof TypeError &&
                        (0, utils_1.isErrnoException)(error) &&
                        (error.code === 'ERR_INVALID_URL' ||
                            error.code === 'ERR_INVALID_FILE_URL_PATH')) {
                        // It's possible for `url` to represent an invalid path for the
                        // current platform. For example, `@import "/foo/bar/baz"` will
                        // resolve to `file:///foo/bar/baz` which is an invalid URL on
                        // Windows. In that case, we treat it as though the file doesn't
                        // exist so that the user's custom importer can still handle the
                        // URL.
                    }
                    else {
                        throw error;
                    }
                }
                if (resolved !== null) {
                    this.prev.push({ url: resolved, path: true });
                    return (0, utils_2.pathToLegacyFileUrl)(resolved);
                }
            }
            return null;
        }
        const prev = this.prev[this.prev.length - 1];
        return (0, utils_1.thenOr)((0, utils_1.thenOr)(this.invokeCallbacks(url, prev.url, options), result => {
            if (result instanceof Error)
                throw result;
            if (result === null)
                return null;
            if (typeof result !== 'object') {
                throw ('Expected importer to return an object, got ' +
                    `${util.inspect(result)}.`);
            }
            if ('contents' in result || !('file' in result)) {
                this.lastContents = result.contents ?? '';
                if ('file' in result) {
                    return new URL(utils_2.legacyImporterProtocol +
                        encodeURI(result.file));
                }
                else if (/^[A-Za-z+.-]+:/.test(url)) {
                    return new URL(`${utils_2.legacyImporterProtocolPrefix}${url}`);
                }
                else {
                    return new URL(utils_2.legacyImporterProtocol + encodeURI(url));
                }
            }
            else {
                if (p.isAbsolute(result.file)) {
                    const resolved = (0, resolve_path_1.resolvePath)(result.file, options.fromImport);
                    return resolved ? (0, utils_2.pathToLegacyFileUrl)(resolved) : null;
                }
                const prefixes = [...this.loadPaths, '.'];
                if (prev.path)
                    prefixes.unshift(p.dirname(prev.url));
                for (const prefix of prefixes) {
                    const resolved = (0, resolve_path_1.resolvePath)(p.join(prefix, result.file), options.fromImport);
                    if (resolved !== null)
                        return (0, utils_2.pathToLegacyFileUrl)(resolved);
                }
                return null;
            }
        }), result => {
            if (result !== null) {
                const path = result.protocol === exports.legacyImporterFileProtocol;
                this.prev.push({
                    url: path ? (0, utils_2.legacyFileUrlToPath)(result) : url,
                    path,
                });
                return result;
            }
            else {
                for (const loadPath of this.loadPaths) {
                    const resolved = (0, resolve_path_1.resolvePath)(p.join(loadPath, url), options.fromImport);
                    if (resolved !== null)
                        return (0, utils_2.pathToLegacyFileUrl)(resolved);
                }
                return null;
            }
        });
    }
    load(canonicalUrl) {
        if (canonicalUrl.protocol === exports.endOfLoadProtocol) {
            this.prev.pop();
            return {
                contents: '',
                syntax: 'scss',
                sourceMapUrl: new URL(exports.endOfLoadProtocol),
            };
        }
        if (canonicalUrl.protocol === exports.legacyImporterFileProtocol) {
            const syntax = canonicalUrl.pathname.endsWith('.sass')
                ? 'indented'
                : canonicalUrl.pathname.endsWith('.css')
                    ? 'css'
                    : 'scss';
            let contents = this.lastContents ??
                fs.readFileSync((0, utils_2.legacyFileUrlToPath)(canonicalUrl), 'utf-8');
            this.lastContents = undefined;
            if (syntax === 'css') {
                this.prev.pop();
            }
            else {
                contents = this.wrapContents(contents, syntax);
            }
            return { contents, syntax, sourceMapUrl: canonicalUrl };
        }
        const lastContents = this.lastContents;
        assert_1.strict.notEqual(lastContents, undefined);
        this.lastContents = undefined;
        return {
            contents: this.wrapContents(lastContents, 'scss'),
            syntax: 'scss',
            sourceMapUrl: canonicalUrl,
        };
    }
    // Invokes each callback in `this.callbacks` until one returns a non-null
    // `LegacyImporterResult`, then returns that result. Returns `null` if all
    // callbacks return `null`.
    invokeCallbacks(url, prev, { fromImport }) {
        (0, assert_1.strict)(this.callbacks.length > 0);
        const self = { ...this.self, fromImport };
        self.options = { ...self.options, context: self };
        const invokeNthCallback = (n) => (0, utils_1.thenOr)(this.invokeCallback(this.callbacks[n], self, url, prev), result => {
            if (result === null) {
                if (n === this.callbacks.length - 1)
                    return null;
                return invokeNthCallback(n + 1);
            }
            if ('contents' in result &&
                result.contents &&
                typeof result.contents !== 'string') {
                throw new Error(`Invalid argument (contents): must be a string but was: ${result.contents.constructor.name}`);
            }
            return result;
        });
        return invokeNthCallback(0);
    }
    // Invokes `callback` and converts its return value into a `PromiseOr`.
    invokeCallback(callback, self, url, prev) {
        if (this.sync) {
            return callback.call(self, url, prev);
        }
        return new Promise(resolve => {
            // The cast here is necesary to work around microsoft/TypeScript#33815.
            const syncResult = callback.call(self, url, prev, resolve);
            if (syncResult !== undefined)
                resolve(syncResult);
        });
    }
    // Modifies {@link contents} to ensure that we know when a load has completed
    // so we can pass the correct `prev` argument to callbacks.
    wrapContents(contents, syntax) {
        const url = `"${exports.endOfLoadProtocol}${endOfLoadCount++}"`;
        if (syntax === 'scss') {
            return (`@use "sass:meta" as ${exports.metaNamespace};` +
                contents +
                `\n;@include ${exports.metaNamespace}.load-css(${url});`);
        }
        else {
            return (`@use "sass:meta" as ${exports.metaNamespace}\n` +
                contents +
                `\n@include ${exports.metaNamespace}.load-css(${url})`);
        }
    }
}
exports.LegacyImporterWrapper = LegacyImporterWrapper;
//# sourceMappingURL=importer.js.map