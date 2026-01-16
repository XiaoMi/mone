import type { ExtractPropTypes } from 'vue';
export declare const textProps: {
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
    readonly size: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "small" | "default" | "large", unknown, "", boolean>;
    readonly truncated: BooleanConstructor;
    readonly lineClamp: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly tag: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "span", boolean>;
};
export type TextProps = ExtractPropTypes<typeof textProps>;
