"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.run = run;
const fs = require("fs");
const p = require("path");
/**
 * Runs `test` within a sandbox directory. This directory is made available via
 * the `dir` function, which acts like `p.join()` but includes the sandbox
 * directory at the beginning.
 *
 * Handles all buildup and teardown. Returns a promise that resolves when `test`
 * finishes running.
 */
async function run(test, options) {
    const testDir = p.join(p.dirname(__filename), 'sandbox', `${Math.random()}`.slice(2));
    fs.mkdirSync(testDir, { recursive: true });
    if (options?.sassPathDirs) {
        process.env.SASS_PATH = options.sassPathDirs.join(process.platform === 'win32' ? ';' : ':');
    }
    try {
        await test((...paths) => p.join(testDir, ...paths));
    }
    finally {
        if (options?.sassPathDirs)
            process.env.SASS_PATH = undefined;
        fs.rmSync(testDir, { force: true, recursive: true, maxRetries: 3 });
    }
}
//# sourceMappingURL=sandbox.js.map