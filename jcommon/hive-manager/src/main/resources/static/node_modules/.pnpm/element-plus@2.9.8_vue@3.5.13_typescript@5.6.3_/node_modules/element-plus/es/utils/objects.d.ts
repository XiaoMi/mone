import type { Entries } from 'type-fest';
import type { Arrayable } from '.';
export declare const keysOf: <T extends object>(arr: T) => Array<keyof T>;
export declare const entriesOf: <T extends object>(arr: T) => Entries<T>;
export { hasOwn } from '@vue/shared';
export declare const getProp: <T = any>(obj: Record<string, any>, path: Arrayable<string>, defaultValue?: any) => {
    value: T;
};
