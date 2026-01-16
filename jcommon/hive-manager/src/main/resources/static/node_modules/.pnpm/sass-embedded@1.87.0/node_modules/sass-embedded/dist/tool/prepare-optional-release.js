"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.nodePlatformToDartPlatform = nodePlatformToDartPlatform;
exports.nodeArchToDartArch = nodeArchToDartArch;
const extractZip = require("extract-zip");
const fs_1 = require("fs");
const p = require("path");
const tar_1 = require("tar");
const yargs_1 = require("yargs");
const pkg = require("../package.json");
const utils = require("./utils");
const argv = (0, yargs_1.default)(process.argv.slice(2))
    .option('package', {
    type: 'string',
    description: 'Directory name under `npm` directory that contains optional dependencies.',
    demandOption: true,
    choices: Object.keys(pkg.optionalDependencies).map(name => name.split('sass-embedded-')[1]),
})
    .parseSync();
// Converts a Node-style platform name as returned by `process.platform` into a
// name used by Dart Sass. Throws if the operating system is not supported by
// Dart Sass Embedded.
function nodePlatformToDartPlatform(platform) {
    switch (platform) {
        case 'android':
            return 'android';
        case 'linux':
        case 'linux-musl':
            return 'linux';
        case 'darwin':
            return 'macos';
        case 'win32':
            return 'windows';
        default:
            throw Error(`Platform ${platform} is not supported.`);
    }
}
// Converts a Node-style architecture name as returned by `process.arch` into a
// name used by Dart Sass. Throws if the architecture is not supported by Dart
// Sass Embedded.
function nodeArchToDartArch(arch) {
    switch (arch) {
        case 'ia32':
            return 'ia32';
        case 'x86':
            return 'ia32';
        case 'x64':
            return 'x64';
        case 'arm':
            return 'arm';
        case 'arm64':
            return 'arm64';
        case 'riscv64':
            return 'riscv64';
        default:
            throw Error(`Architecture ${arch} is not supported.`);
    }
}
// Get the platform's file extension for archives.
function getArchiveExtension(platform) {
    return platform === 'windows' ? '.zip' : '.tar.gz';
}
// Downloads the release for `repo` located at `assetUrl`, then unzips it into
// `outPath`.
async function downloadRelease(options) {
    console.log(`Downloading ${options.repo} release asset.`);
    const response = await fetch(options.assetUrl, {
        redirect: 'follow',
    });
    if (!response.ok) {
        throw Error(`Failed to download ${options.repo} release asset: ${response.statusText}`);
    }
    const releaseAsset = Buffer.from(await response.arrayBuffer());
    console.log(`Unzipping ${options.repo} release asset to ${options.outPath}.`);
    await utils.cleanDir(p.join(options.outPath, options.repo));
    const archiveExtension = options.assetUrl.endsWith('.zip')
        ? '.zip'
        : '.tar.gz';
    const zippedAssetPath = options.outPath + '/' + options.repo + archiveExtension;
    await fs_1.promises.writeFile(zippedAssetPath, releaseAsset);
    if (archiveExtension === '.zip') {
        await extractZip(zippedAssetPath, {
            dir: p.join(process.cwd(), options.outPath),
        });
    }
    else {
        (0, tar_1.extract)({
            file: zippedAssetPath,
            cwd: options.outPath,
            sync: true,
        });
    }
    await fs_1.promises.unlink(zippedAssetPath);
}
void (async () => {
    try {
        const version = pkg['compiler-version'];
        if (version.endsWith('-dev')) {
            throw Error("Can't release optional packages for a -dev compiler version.");
        }
        const index = argv.package.lastIndexOf('-');
        const nodePlatform = argv.package.substring(0, index);
        const nodeArch = argv.package.substring(index + 1);
        const dartPlatform = nodePlatformToDartPlatform(nodePlatform);
        const dartArch = nodeArchToDartArch(nodeArch);
        const isMusl = nodePlatform === 'linux-musl';
        const outPath = p.join('npm', argv.package);
        await downloadRelease({
            repo: 'dart-sass',
            assetUrl: 'https://github.com/sass/dart-sass/releases/download/' +
                `${version}/dart-sass-${version}-` +
                `${dartPlatform}-${dartArch}${isMusl ? '-musl' : ''}` +
                `${getArchiveExtension(dartPlatform)}`,
            outPath,
        });
    }
    catch (error) {
        console.error(error);
        process.exitCode = 1;
    }
})();
//# sourceMappingURL=prepare-optional-release.js.map