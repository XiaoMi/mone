import { capitalize as capitalize$1, hyphenate } from '@vue/shared';
export { camelize, hyphenate } from '@vue/shared';

const kebabCase = hyphenate;
const escapeStringRegexp = (string = "") => string.replace(/[|\\{}()[\]^$+*?.]/g, "\\$&").replace(/-/g, "\\x2d");
const capitalize = (str) => capitalize$1(str);

export { capitalize, escapeStringRegexp, kebabCase };
//# sourceMappingURL=strings.mjs.map
