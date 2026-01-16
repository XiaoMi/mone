"use strict";
// Copyright 2023 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.legacyImporterProtocolPrefix = exports.legacyImporterProtocol = void 0;
exports.removeLegacyImporter = removeLegacyImporter;
exports.removeLegacyImporterFromSpan = removeLegacyImporterFromSpan;
exports.pathToLegacyFileUrl = pathToLegacyFileUrl;
exports.legacyFileUrlToPath = legacyFileUrlToPath;
const assert_1 = require("assert");
const url_1 = require("url");
const utils_1 = require("../utils");
const importer_1 = require("./importer");
/**
 * The URL protocol to use for URLs canonicalized using `LegacyImporterWrapper`.
 */
exports.legacyImporterProtocol = 'legacy-importer:';
/**
 * The prefix for absolute URLs canonicalized using `LegacyImporterWrapper`.
 *
 * This is used to distinguish imports resolved relative to URLs returned by a
 * legacy importer from manually-specified absolute URLs.
 */
exports.legacyImporterProtocolPrefix = 'legacy-importer-';
// A regular expression that matches legacy importer protocol syntax that
// should be removed from human-readable messages.
const removeLegacyImporterRegExp = new RegExp(`${exports.legacyImporterProtocol}|${exports.legacyImporterProtocolPrefix}`, 'g');
// Returns `string` with all instances of legacy importer syntax removed.
function removeLegacyImporter(string) {
    return string.replace(removeLegacyImporterRegExp, '');
}
// Returns a copy of [span] with the URL updated to remove legacy importer
// syntax.
function removeLegacyImporterFromSpan(span) {
    if (!span.url)
        return span;
    return {
        ...span,
        url: new URL(removeLegacyImporter(span.url.toString()), (0, url_1.pathToFileURL)(process.cwd())),
    };
}
// Converts [path] to a `file:` URL and adds the [legacyImporterProtocolPrefix]
// to the beginning so we can distinguish it from manually-specified absolute
// `file:` URLs.
function pathToLegacyFileUrl(path) {
    return new URL(`${exports.legacyImporterProtocolPrefix}${(0, url_1.pathToFileURL)(path)}`);
}
// Converts a `file:` URL with [legacyImporterProtocolPrefix] to the filesystem
// path which it represents.
function legacyFileUrlToPath(url) {
    assert_1.strict.equal(url.protocol, importer_1.legacyImporterFileProtocol);
    const originalUrl = url
        .toString()
        .substring(exports.legacyImporterProtocolPrefix.length);
    return (0, utils_1.fileUrlToPathCrossPlatform)(originalUrl);
}
//# sourceMappingURL=utils.js.map