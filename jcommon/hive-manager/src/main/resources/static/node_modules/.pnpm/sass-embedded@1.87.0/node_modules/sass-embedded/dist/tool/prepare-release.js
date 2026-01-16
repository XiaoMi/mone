"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = require("fs");
const shell = require("shelljs");
const pkg = require("../package.json");
const get_deprecations_1 = require("./get-deprecations");
const get_language_repo_1 = require("./get-language-repo");
void (async () => {
    try {
        await sanityCheckBeforeRelease();
        await (0, get_language_repo_1.getLanguageRepo)('lib/src/vendor');
        await (0, get_deprecations_1.getDeprecations)('lib/src/vendor');
        console.log('Transpiling TS into dist.');
        shell.exec('tsc -p tsconfig.build.json');
        shell.cp('lib/index.mjs', 'dist/lib/index.mjs');
        console.log('Copying JS API types to dist.');
        shell.cp('-R', 'lib/src/vendor/sass', 'dist/types');
        shell.cp('dist/types/index.d.ts', 'dist/types/index.m.d.ts');
        await fs_1.promises.unlink('dist/types/README.md');
        console.log('Ready for publishing to npm.');
    }
    catch (error) {
        console.error(error);
        process.exitCode = 1;
    }
})();
// Quick sanity checks to make sure the release we are preparing is a suitable
// candidate for release.
async function sanityCheckBeforeRelease() {
    console.log('Running sanity checks before releasing.');
    const releaseVersion = pkg.version;
    const ref = process.env['GITHUB_REF'];
    if (ref !== `refs/tags/${releaseVersion}`) {
        throw Error(`GITHUB_REF ${ref} is different than the package.json version ${releaseVersion}.`);
    }
    for (const [dep, version] of Object.entries(pkg.optionalDependencies)) {
        if (version !== releaseVersion) {
            throw Error(`optional dependency ${dep}'s version doesn't match ${releaseVersion}.`);
        }
    }
    if (releaseVersion.indexOf('-dev') > 0) {
        throw Error(`${releaseVersion} is a dev release.`);
    }
    const versionHeader = new RegExp(`^## ${releaseVersion}$`, 'm');
    const changelog = await fs_1.promises.readFile('CHANGELOG.md', 'utf8');
    if (!changelog.match(versionHeader)) {
        throw Error(`There's no CHANGELOG entry for ${releaseVersion}.`);
    }
}
//# sourceMappingURL=prepare-release.js.map