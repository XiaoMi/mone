"use strict";
// Generates the list of deprecations from spec/deprecations.yaml in the
// language repo.
Object.defineProperty(exports, "__esModule", { value: true });
exports.getDeprecations = getDeprecations;
const fs = require("fs");
const yaml_1 = require("yaml");
const yamlFile = 'build/sass/spec/deprecations.yaml';
/**
 * Converts a version string in the form X.Y.Z to be code calling the Version
 * constructor, or null if the string is undefined.
 */
function toVersionCode(version) {
    if (!version)
        return 'null';
    const match = version.match(/^(\d+)\.(\d+)\.(\d+)$/);
    if (match === null) {
        throw new Error(`Invalid version ${version}`);
    }
    return `new Version(${match[1]}, ${match[2]}, ${match[3]})`;
}
/**
 * Generates the list of deprecations based on the YAML file in the language
 * repo.
 */
async function getDeprecations(outDirectory) {
    const yamlText = fs.readFileSync(yamlFile, 'utf8');
    const deprecations = (0, yaml_1.parse)(yamlText);
    let tsText = "import {Deprecations} from './sass';\n" +
        "import {Version} from '../version';\n\n" +
        'export const deprecations: Deprecations = {\n';
    for (const [id, deprecation] of Object.entries(deprecations)) {
        const key = id.includes('-') ? `'${id}'` : id;
        const dartSass = deprecation['dart-sass'];
        tsText +=
            `  ${key}: {\n` +
                `    id: '${id}',\n` +
                `    description: '${deprecation.description}',\n` +
                `    status: '${dartSass.status}',\n` +
                `    deprecatedIn: ${toVersionCode(dartSass.deprecated)},\n` +
                `    obsoleteIn: ${toVersionCode(dartSass.obsolete)},\n` +
                '  },\n';
    }
    tsText +=
        "  'user-authored': {\n" +
            "    id: 'user-authored',\n" +
            "    status: 'user',\n" +
            '    deprecatedIn: null,\n' +
            '    obsoleteIn: null,\n' +
            '  },\n' +
            '}\n';
    fs.writeFileSync(`${outDirectory}/deprecations.ts`, tsText);
}
//# sourceMappingURL=get-deprecations.js.map