"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
const yargs_1 = require("yargs");
const get_deprecations_1 = require("./get-deprecations");
const get_embedded_compiler_1 = require("./get-embedded-compiler");
const get_language_repo_1 = require("./get-language-repo");
const argv = (0, yargs_1.default)(process.argv.slice(2))
    .option('compiler-path', {
    type: 'string',
    description: 'Build the Embedded Dart Sass binary from the source at this path.',
})
    .option('compiler-ref', {
    type: 'string',
    description: 'Build the Embedded Dart Sass binary from this Git ref.',
})
    .option('skip-compiler', {
    type: 'boolean',
    description: "Don't Embedded Dart Sass at all.",
})
    .option('language-path', {
    type: 'string',
    description: 'Use the Sass language repo from the source at this path.',
})
    .option('language-ref', {
    type: 'string',
    description: 'Use the Sass language repo from this Git ref.',
})
    .conflicts({
    'compiler-path': ['compiler-ref', 'skip-compiler'],
    'compiler-ref': ['skip-compiler'],
    'language-path': ['language-ref'],
})
    .parseSync();
void (async () => {
    try {
        const outPath = 'lib/src/vendor';
        if (argv['language-ref']) {
            await (0, get_language_repo_1.getLanguageRepo)(outPath, {
                ref: argv['language-ref'],
            });
        }
        else if (argv['language-path']) {
            await (0, get_language_repo_1.getLanguageRepo)(outPath, {
                path: argv['language-path'],
            });
        }
        else {
            await (0, get_language_repo_1.getLanguageRepo)(outPath);
        }
        if (!argv['skip-compiler']) {
            if (argv['compiler-ref']) {
                await (0, get_embedded_compiler_1.getEmbeddedCompiler)(outPath, {
                    ref: argv['compiler-ref'],
                });
            }
            else if (argv['compiler-path']) {
                await (0, get_embedded_compiler_1.getEmbeddedCompiler)(outPath, {
                    path: argv['compiler-path'],
                });
            }
            else {
                await (0, get_embedded_compiler_1.getEmbeddedCompiler)(outPath);
            }
        }
        await (0, get_deprecations_1.getDeprecations)(outPath);
    }
    catch (error) {
        console.error(error);
        process.exitCode = 1;
    }
})();
//# sourceMappingURL=init.js.map