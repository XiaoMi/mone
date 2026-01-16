"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.resolvePath = resolvePath;
const fs = require("fs");
const p = require("path");
/**
 * Resolves a path using the same logic as the filesystem importer.
 *
 * This tries to fill in extensions and partial prefixes and check for a
 * directory default. If no file can be found, it returns `null`.
 */
function resolvePath(path, fromImport) {
    const extension = p.extname(path);
    if (extension === '.sass' || extension === '.scss' || extension === '.css') {
        return ((fromImport
            ? exactlyOne(tryPath(`${withoutExtension(path)}.import${extension}`))
            : null) ?? exactlyOne(tryPath(path)));
    }
    return ((fromImport ? exactlyOne(tryPathWithExtensions(`${path}.import`)) : null) ??
        exactlyOne(tryPathWithExtensions(path)) ??
        tryPathAsDirectory(path, fromImport));
}
// Like `tryPath`, but checks `.sass`, `.scss`, and `.css` extensions.
function tryPathWithExtensions(path) {
    const result = [...tryPath(path + '.sass'), ...tryPath(path + '.scss')];
    return result.length > 0 ? result : tryPath(path + '.css');
}
// Returns the `path` and/or the partial with the same name, if either or both
// exists. If neither exists, returns an empty list.
function tryPath(path) {
    const partial = p.join(p.dirname(path), `_${p.basename(path)}`);
    const result = [];
    if (fileExists(partial))
        result.push(partial);
    if (fileExists(path))
        result.push(path);
    return result;
}
// Returns the resolved index file for `path` if `path` is a directory and the
// index file exists. Otherwise, returns `null`.
function tryPathAsDirectory(path, fromImport) {
    if (!dirExists(path))
        return null;
    return ((fromImport
        ? exactlyOne(tryPathWithExtensions(p.join(path, 'index.import')))
        : null) ?? exactlyOne(tryPathWithExtensions(p.join(path, 'index'))));
}
// If `paths` contains exactly one path, returns that path. If it contains no
// paths, returns `null`. If it contains more than one, throws an exception.
function exactlyOne(paths) {
    if (paths.length === 0)
        return null;
    if (paths.length === 1)
        return paths[0];
    throw new Error("It's not clear which file to import. Found:\n" +
        paths.map(path => '  ' + path).join('\n'));
}
// Returns whether or not a file (not a directory) exists at `path`.
function fileExists(path) {
    // `existsSync()` is faster than `statSync()`, but it doesn't clarify whether
    // the entity in question is a file or a directory. Since false negatives are
    // much more common than false positives, it works out in our favor to check
    // this first.
    if (!fs.existsSync(path))
        return false;
    try {
        return fs.statSync(path).isFile();
    }
    catch (error) {
        if (error.code === 'ENOENT')
            return false;
        throw error;
    }
}
// Returns whether or not a directory (not a file) exists at `path`.
function dirExists(path) {
    // `existsSync()` is faster than `statSync()`, but it doesn't clarify whether
    // the entity in question is a file or a directory. Since false negatives are
    // much more common than false positives, it works out in our favor to check
    // this first.
    if (!fs.existsSync(path))
        return false;
    try {
        return fs.statSync(path).isDirectory();
    }
    catch (error) {
        if (error.code === 'ENOENT')
            return false;
        throw error;
    }
}
// Returns `path` without its file extension.
function withoutExtension(path) {
    const extension = p.extname(path);
    return path.substring(0, path.length - extension.length);
}
//# sourceMappingURL=resolve-path.js.map