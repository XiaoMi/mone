"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.render = render;
exports.renderSync = renderSync;
const fs = require("fs");
const p = require("path");
const url_1 = require("url");
const importer_registry_1 = require("../importer-registry");
const exception_1 = require("../exception");
const compile_1 = require("../compile");
const deprecations_1 = require("../deprecations");
const utils_1 = require("../utils");
const wrap_1 = require("./value/wrap");
const importer_1 = require("./importer");
const utils_2 = require("./utils");
function render(options, callback) {
    try {
        options = adjustOptions(options);
        const start = Date.now();
        (0, deprecations_1.warnForHostSideDeprecation)('The legacy JS API is deprecated and will be removed in ' +
            'Dart Sass 2.0.0.\n\n' +
            'More info: https://sass-lang.com/d/legacy-js-api', deprecations_1.deprecations['legacy-js-api'], options);
        const compileSass = isStringOptions(options)
            ? (0, compile_1.compileStringAsync)(options.data, convertStringOptions(options, false))
            : (0, compile_1.compileAsync)(options.file, convertOptions(options, false));
        compileSass.then(result => callback(undefined, newLegacyResult(options, start, result)), error => callback(newLegacyException(error)));
    }
    catch (error) {
        if (error instanceof Error)
            callback(newLegacyException(error));
        throw error;
    }
}
function renderSync(options) {
    const start = Date.now();
    try {
        options = adjustOptions(options);
        (0, deprecations_1.warnForHostSideDeprecation)('The legacy JS API is deprecated and will be removed in ' +
            'Dart Sass 2.0.0.\n\n' +
            'More info: https://sass-lang.com/d/legacy-js-api', deprecations_1.deprecations['legacy-js-api'], options);
        const result = isStringOptions(options)
            ? (0, compile_1.compileString)(options.data, convertStringOptions(options, true))
            : (0, compile_1.compile)(options.file, convertOptions(options, true));
        return newLegacyResult(options, start, result);
    }
    catch (error) {
        throw newLegacyException(error);
    }
}
// Does some initial adjustments of `options` to make it easier to pass to the
// new API.
function adjustOptions(options) {
    if (!('file' in options && options.file) && !('data' in options)) {
        throw new Error('Either options.data or options.file must be set.');
    }
    // In legacy API, the current working directory is always attempted before
    // any load path.
    options.includePaths = [process.cwd(), ...(options.includePaths ?? [])];
    if (!isStringOptions(options) &&
        // The `indentedSyntax` option takes precedence over the file extension in the
        // legacy API, but the new API doesn't have a `syntax` option for a file path.
        // Instead, we eagerly load the entrypoint into memory and treat it like a
        // string source.
        (options.indentedSyntax !==
            undefined ||
            options.importer)) {
        return {
            ...options,
            data: fs.readFileSync(options.file, 'utf8'),
            indentedSyntax: !!options
                .indentedSyntax,
        };
    }
    else {
        return options;
    }
}
// Returns whether `options` is a `LegacyStringOptions`.
function isStringOptions(options) {
    return 'data' in options;
}
// Converts `LegacyOptions` into new API `Options`.
function convertOptions(options, sync) {
    if ('outputStyle' in options &&
        options.outputStyle !== 'compressed' &&
        options.outputStyle !== 'expanded') {
        throw new Error(`Unknown output style: "${options.outputStyle}"`);
    }
    const self = pluginThis(options);
    const functions = {};
    for (let [signature, callback] of Object.entries(options.functions ?? {})) {
        // The legacy API allows signatures without parentheses but the modern API
        // does not.
        if (!signature.includes('('))
            signature += '()';
        functions[signature.trimLeft()] = (0, wrap_1.wrapFunction)(self, callback, sync);
    }
    const importers = options.importer &&
        (!(options.importer instanceof Array) || options.importer.length > 0)
        ? [
            new importer_1.LegacyImporterWrapper(self, options.importer instanceof Array
                ? options.importer
                : [options.importer], options.includePaths ?? [], options.file ?? 'stdin', sync),
        ]
        : undefined;
    return {
        functions,
        importers: options.pkgImporter instanceof importer_registry_1.NodePackageImporter
            ? [options.pkgImporter, ...(importers ?? [])]
            : importers,
        sourceMap: wasSourceMapRequested(options),
        sourceMapIncludeSources: options.sourceMapContents,
        loadPaths: importers ? undefined : options.includePaths,
        style: options.outputStyle,
        quietDeps: options.quietDeps,
        verbose: options.verbose,
        charset: options.charset,
        logger: options.logger,
        fatalDeprecations: options.fatalDeprecations,
        futureDeprecations: options.futureDeprecations,
        silenceDeprecations: options.silenceDeprecations,
        legacy: true,
    };
}
// Converts `LegacyStringOptions` into new API `StringOptions`.
function convertStringOptions(options, sync) {
    const modernOptions = convertOptions(options, sync);
    // Use a no-op base importer, because the LegacyImporterWrapper will emulate
    // the base importer by itself in order to mark containingUrl as accessed.
    const importer = modernOptions.importers?.some(importer => importer instanceof importer_1.LegacyImporterWrapper)
        ? {
            canonicalize() {
                return null;
            },
            load() {
                return null;
            },
        }
        : undefined;
    return {
        ...modernOptions,
        url: options.file
            ? options.importer
                ? (0, utils_2.pathToLegacyFileUrl)(options.file)
                : (0, url_1.pathToFileURL)(options.file)
            : new url_1.URL(utils_2.legacyImporterProtocol),
        importer,
        syntax: options.indentedSyntax ? 'indented' : 'scss',
    };
}
// Determines whether a sourceMap was requested by the call to `render()`.
function wasSourceMapRequested(options) {
    return (typeof options.sourceMap === 'string' ||
        (options.sourceMap === true && !!options.outFile));
}
// Creates the `this` value that's used for callbacks.
function pluginThis(options) {
    const pluginThis = {
        options: {
            context: undefined,
            file: options.file,
            data: options.data,
            includePaths: (options.includePaths ?? []).join(p.delimiter),
            precision: 10,
            style: 1,
            indentType: 0,
            indentWidth: 2,
            linefeed: '\n',
            result: {
                stats: {
                    start: Date.now(),
                    entry: options.file ?? 'data',
                },
            },
        },
    };
    pluginThis.options.context = pluginThis;
    return pluginThis;
}
// Transforms the compilation result into an object that mimics the Node Sass
// API format.
function newLegacyResult(options, start, result) {
    const end = Date.now();
    let css = result.css;
    let sourceMapBytes;
    if (result.sourceMap) {
        const sourceMap = result.sourceMap;
        sourceMap.sourceRoot = options.sourceMapRoot ?? '';
        const sourceMapPath = typeof options.sourceMap === 'string'
            ? options.sourceMap
            : options.outFile + '.map';
        const sourceMapDir = p.dirname(sourceMapPath);
        if (options.outFile) {
            sourceMap.file = (0, utils_1.pathToUrlString)(p.relative(sourceMapDir, options.outFile));
        }
        else if (options.file) {
            sourceMap.file = (0, utils_1.pathToUrlString)((0, utils_1.withoutExtension)(options.file) + '.css');
        }
        else {
            sourceMap.file = 'stdin.css';
        }
        sourceMap.sources = sourceMap.sources
            .filter(source => !source.startsWith(importer_1.endOfLoadProtocol))
            .map(source => {
            source = (0, utils_2.removeLegacyImporter)(source);
            if (source.startsWith('file://')) {
                return (0, utils_1.pathToUrlString)(p.relative(sourceMapDir, (0, utils_1.fileUrlToPathCrossPlatform)(source)));
            }
            else if (source.startsWith('data:')) {
                return 'stdin';
            }
            else {
                return source;
            }
        });
        sourceMapBytes = Buffer.from(JSON.stringify(sourceMap));
        if (!options.omitSourceMapUrl) {
            let url;
            if (options.sourceMapEmbed) {
                url = `data:application/json;base64,${sourceMapBytes.toString('base64')}`;
            }
            else if (options.outFile) {
                url = (0, utils_1.pathToUrlString)(p.relative(p.dirname(options.outFile), sourceMapPath));
            }
            else {
                url = (0, utils_1.pathToUrlString)(sourceMapPath);
            }
            css += `\n\n/*# sourceMappingURL=${url} */`;
        }
    }
    return {
        css: Buffer.from(css),
        map: sourceMapBytes,
        stats: {
            entry: options.file ?? 'data',
            start,
            end,
            duration: end - start,
            includedFiles: result.loadedUrls
                .filter(url => url.protocol !== importer_1.endOfLoadProtocol)
                .map(url => {
                if (url.protocol === utils_2.legacyImporterProtocol) {
                    return decodeURI(url.pathname);
                }
                const urlString = (0, utils_2.removeLegacyImporter)(url.toString());
                return urlString.startsWith('file:')
                    ? (0, utils_1.fileUrlToPathCrossPlatform)(urlString)
                    : urlString;
            }),
        },
    };
}
// Decorates an Error with additional fields so that it behaves like a Node Sass
// error.
function newLegacyException(error) {
    if (!(error instanceof exception_1.Exception)) {
        return Object.assign(error, {
            formatted: error.toString(),
            status: 3,
        });
    }
    const span = error.span ? (0, utils_2.removeLegacyImporterFromSpan)(error.span) : null;
    let file;
    if (!span?.url) {
        file = 'stdin';
    }
    else if (span.url.protocol === 'file:') {
        // We have to cast to Node's URL type here because the specified type is the
        // standard URL type which is slightly less featureful. `fileURLToPath()`
        // does work with standard URL objects in practice, but we know that we
        // generate Node URLs here regardless.
        file = (0, utils_1.fileUrlToPathCrossPlatform)(span.url);
    }
    else {
        file = span.url.toString();
    }
    const errorString = (0, utils_2.removeLegacyImporter)(error.toString());
    return Object.assign(new Error(), {
        status: 1,
        message: errorString.replace(/^Error: /, ''),
        formatted: errorString,
        toString: () => errorString,
        stack: error.stack ? (0, utils_2.removeLegacyImporter)(error.stack) : undefined,
        line: (0, utils_1.isNullOrUndefined)(error.span?.start.line)
            ? undefined
            : error.span.start.line + 1,
        column: (0, utils_1.isNullOrUndefined)(error.span?.start.column)
            ? undefined
            : error.span.start.column + 1,
        file,
    });
}
//# sourceMappingURL=index.js.map