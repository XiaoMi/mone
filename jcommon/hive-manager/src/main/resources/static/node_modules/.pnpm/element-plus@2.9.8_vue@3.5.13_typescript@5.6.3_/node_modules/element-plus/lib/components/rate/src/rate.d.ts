import type { Component, ExtractPropTypes } from 'vue';
import type Rate from './rate.vue';
export declare const rateProps: {
    readonly ariaLabel: StringConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly id: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly lowThreshold: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 2, boolean>;
    readonly highThreshold: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 4, boolean>;
    readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 5, boolean>;
    readonly colors: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string[] | Record<number, string>) | (() => string[] | Record<number, string>) | ((new (...args: any[]) => string[] | Record<number, string>) | (() => string[] | Record<number, string>))[], unknown, unknown, () => ["", "", ""], boolean>;
    readonly voidColor: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly disabledVoidColor: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly icons: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component)[] | Record<number, string | Component>) | (() => (string | Component)[] | Record<number, string | Component>) | ((new (...args: any[]) => (string | Component)[] | Record<number, string | Component>) | (() => (string | Component)[] | Record<number, string | Component>))[], unknown, unknown, () => [Component, Component, Component], boolean>;
    readonly voidIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown, () => Component, boolean>;
    readonly disabledVoidIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown, () => Component, boolean>;
    readonly disabled: BooleanConstructor;
    readonly allowHalf: BooleanConstructor;
    readonly showText: BooleanConstructor;
    readonly showScore: BooleanConstructor;
    readonly textColor: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly texts: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string[]) | (() => string[]) | ((new (...args: any[]) => string[]) | (() => string[]))[], unknown, unknown, () => ["Extremely bad", "Disappointed", "Fair", "Satisfied", "Surprise"], boolean>;
    readonly scoreTemplate: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "{value}", boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly clearable: BooleanConstructor;
};
export type RateProps = ExtractPropTypes<typeof rateProps>;
export declare const rateEmits: {
    change: (value: number) => boolean;
    "update:modelValue": (value: number) => boolean;
};
export type RateEmits = typeof rateEmits;
export type RateInstance = InstanceType<typeof Rate> & unknown;
