"use strict";
// Copyright 2022 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.getEmbeddedCompiler = getEmbeddedCompiler;
const p = require("path");
const shell = require("shelljs");
const utils = require("./utils");
/**
 * Downloads and builds the Embedded Dart Sass compiler.
 *
 * Can check out and build the source from a Git `ref` or build from the source
 * at `path`. By default, checks out the latest revision from GitHub.
 */
async function getEmbeddedCompiler(outPath, options) {
    const repo = 'dart-sass';
    let source;
    if (!options || 'ref' in options) {
        utils.fetchRepo({
            repo,
            outPath: 'build',
            ref: options?.ref ?? 'main',
        });
        source = p.join('build', repo);
    }
    else {
        source = options.path;
    }
    // Make sure the compiler sees the same version of the language repo that the
    // host is using, but if they're already the same directory (as in the Dart
    // Sass CI environment) we don't need to do anything.
    const languageInHost = p.resolve('build/sass');
    const languageInCompiler = p.resolve(p.join(source, 'build/language'));
    if (!(await utils.sameTarget(languageInHost, languageInCompiler))) {
        await utils.cleanDir(languageInCompiler);
        await utils.link(languageInHost, languageInCompiler);
    }
    buildDartSassEmbedded(source);
    await utils.link(p.join(source, 'build'), p.join(outPath, repo));
}
// Builds the Embedded Dart Sass executable from the source at `repoPath`.
function buildDartSassEmbedded(repoPath) {
    console.log("Downloading Dart Sass's dependencies.");
    shell.exec('dart pub upgrade', {
        cwd: repoPath,
        silent: true,
    });
    console.log('Building the Dart Sass executable.');
    shell.exec('dart run grinder protobuf pkg-standalone-dev', {
        cwd: repoPath,
        env: { ...process.env, UPDATE_SASS_PROTOCOL: 'false' },
    });
}
//# sourceMappingURL=get-embedded-compiler.js.map