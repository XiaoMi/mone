import type { ExtractPropTypes } from 'vue';
import type { ZIndexProperty } from 'csstype';
import type Affix from './affix.vue';
export declare const affixProps: {
    readonly zIndex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => ZIndexProperty) | ((new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => ZIndexProperty))[], unknown, unknown, 100, boolean>;
    readonly target: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly position: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "top", boolean>;
};
export type AffixProps = ExtractPropTypes<typeof affixProps>;
export declare const affixEmits: {
    scroll: ({ scrollTop, fixed }: {
        scrollTop: number;
        fixed: boolean;
    }) => boolean;
    change: (fixed: boolean) => boolean;
};
export type AffixEmits = typeof affixEmits;
export type AffixInstance = InstanceType<typeof Affix> & unknown;
