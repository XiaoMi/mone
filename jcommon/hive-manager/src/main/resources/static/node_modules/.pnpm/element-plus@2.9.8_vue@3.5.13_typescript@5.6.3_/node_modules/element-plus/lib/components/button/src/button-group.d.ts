import type { ExtractPropTypes } from 'vue';
export declare const buttonGroupProps: {
    /**
     * @description control the size of buttons in this button-group
     */
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    /**
     * @description control the type of buttons in this button-group
     */
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
};
export type ButtonGroupProps = ExtractPropTypes<typeof buttonGroupProps>;
