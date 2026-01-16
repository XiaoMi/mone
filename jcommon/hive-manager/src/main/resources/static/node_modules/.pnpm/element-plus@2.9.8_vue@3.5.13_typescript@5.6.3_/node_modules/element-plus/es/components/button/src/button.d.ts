import type { Component, ExtractPropTypes } from 'vue';
export declare const buttonTypes: readonly ["default", "primary", "success", "warning", "info", "danger", "text", ""];
export declare const buttonNativeTypes: readonly ["button", "submit", "reset"];
export declare const buttonProps: {
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly nativeType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "button" | "reset" | "submit", unknown, "button", boolean>;
    readonly loading: BooleanConstructor;
    readonly loadingIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown, () => any, boolean>;
    readonly plain: BooleanConstructor;
    readonly text: BooleanConstructor;
    readonly link: BooleanConstructor;
    readonly bg: BooleanConstructor;
    readonly autofocus: BooleanConstructor;
    readonly round: BooleanConstructor;
    readonly circle: BooleanConstructor;
    readonly color: StringConstructor;
    readonly dark: BooleanConstructor;
    readonly autoInsertSpace: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    readonly tag: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown, "button", boolean>;
};
export declare const buttonEmits: {
    click: (evt: MouseEvent) => boolean;
};
export type ButtonProps = ExtractPropTypes<typeof buttonProps>;
export type ButtonEmits = typeof buttonEmits;
export type ButtonType = ButtonProps['type'];
export type ButtonNativeType = ButtonProps['nativeType'];
export interface ButtonConfigContext {
    autoInsertSpace?: boolean;
}
