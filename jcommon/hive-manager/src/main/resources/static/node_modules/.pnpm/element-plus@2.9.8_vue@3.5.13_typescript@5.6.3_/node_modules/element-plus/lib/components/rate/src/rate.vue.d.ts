import type { Component } from 'vue';
declare function setCurrentValue(value: number, event?: MouseEvent): void;
declare function resetCurrentValue(): void;
declare const _default: import("vue").DefineComponent<{
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
}, {
    /** @description set current value */
    setCurrentValue: typeof setCurrentValue;
    /** @description reset current value */
    resetCurrentValue: typeof resetCurrentValue;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: number) => void;
    change: (value: number) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
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
}>> & {
    "onUpdate:modelValue"?: ((value: number) => any) | undefined;
    onChange?: ((value: number) => any) | undefined;
}, {
    readonly disabled: boolean;
    readonly id: string;
    readonly modelValue: number;
    readonly max: number;
    readonly clearable: boolean;
    readonly textColor: string;
    readonly colors: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string[] | Record<number, string>) | (() => string[] | Record<number, string>) | ((new (...args: any[]) => string[] | Record<number, string>) | (() => string[] | Record<number, string>))[], unknown, unknown>;
    readonly showText: boolean;
    readonly lowThreshold: number;
    readonly highThreshold: number;
    readonly voidColor: string;
    readonly disabledVoidColor: string;
    readonly icons: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | Component)[] | Record<number, string | Component>) | (() => (string | Component)[] | Record<number, string | Component>) | ((new (...args: any[]) => (string | Component)[] | Record<number, string | Component>) | (() => (string | Component)[] | Record<number, string | Component>))[], unknown, unknown>;
    readonly voidIcon: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown>;
    readonly disabledVoidIcon: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown>;
    readonly texts: string[];
    readonly scoreTemplate: string;
    readonly allowHalf: boolean;
    readonly showScore: boolean;
}>;
export default _default;
