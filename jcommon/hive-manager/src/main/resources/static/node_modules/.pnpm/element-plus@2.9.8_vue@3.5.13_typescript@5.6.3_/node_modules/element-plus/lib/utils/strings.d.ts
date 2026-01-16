import { camelize, hyphenate } from '@vue/shared';
export { camelize, hyphenate };
export declare const kebabCase: (str: string) => string;
/**
 * fork from {@link https://github.com/sindresorhus/escape-string-regexp}
 */
export declare const escapeStringRegexp: (string?: string) => string;
export declare const capitalize: <T extends string>(str: T) => Capitalize<T>;
