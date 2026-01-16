import type { ExtractPropTypes } from 'vue';
import type Divider from './divider.vue';
export type BorderStyle = CSSStyleDeclaration['borderStyle'];
export declare const dividerProps: {
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly contentPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "center" | "left" | "right", unknown, "center", boolean>;
    readonly borderStyle: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => string) | ((new (...args: any[]) => string) | (() => string))[], unknown, unknown, "solid", boolean>;
};
export type DividerProps = ExtractPropTypes<typeof dividerProps>;
export type DividerInstance = InstanceType<typeof Divider> & unknown;
