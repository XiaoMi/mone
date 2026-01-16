import type { ExtractPropTypes } from 'vue';
import type { ObjectFitProperty } from 'csstype';
export declare const avatarProps: {
    readonly size: import("element-plus/es/utils").EpPropFinalized<readonly [NumberConstructor, StringConstructor], "" | "small" | "default" | "large", number, "", boolean>;
    readonly shape: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "circle" | "square", unknown, "circle", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly src: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly alt: StringConstructor;
    readonly srcSet: StringConstructor;
    readonly fit: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fill" | "contain" | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "none" | "cover" | "scale-down") | (() => ObjectFitProperty) | ((new (...args: any[]) => "fill" | "contain" | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "none" | "cover" | "scale-down") | (() => ObjectFitProperty))[], unknown, unknown, "cover", boolean>;
};
export type AvatarProps = ExtractPropTypes<typeof avatarProps>;
export declare const avatarEmits: {
    error: (evt: Event) => boolean;
};
export type AvatarEmits = typeof avatarEmits;
