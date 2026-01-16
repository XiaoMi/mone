import type Tag from './tag.vue';
import type { ExtractPropTypes } from 'vue';
export declare const tagProps: {
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "primary", boolean>;
    readonly closable: BooleanConstructor;
    readonly disableTransitions: BooleanConstructor;
    readonly hit: BooleanConstructor;
    readonly color: StringConstructor;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly effect: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "dark" | "light" | "plain", unknown, "light", boolean>;
    readonly round: BooleanConstructor;
};
export type TagProps = ExtractPropTypes<typeof tagProps>;
export declare const tagEmits: {
    close: (evt: MouseEvent) => boolean;
    click: (evt: MouseEvent) => boolean;
};
export type TagEmits = typeof tagEmits;
export type TagInstance = InstanceType<typeof Tag> & unknown;
