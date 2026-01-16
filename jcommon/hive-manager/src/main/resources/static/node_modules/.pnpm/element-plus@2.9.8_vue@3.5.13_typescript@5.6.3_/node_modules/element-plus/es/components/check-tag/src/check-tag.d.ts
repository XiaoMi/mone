import type CheckTag from './check-tag.vue';
import type { ExtractPropTypes } from 'vue';
export declare const checkTagProps: {
    readonly checked: BooleanConstructor;
    readonly disabled: BooleanConstructor;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "primary", boolean>;
};
export type CheckTagProps = ExtractPropTypes<typeof checkTagProps>;
export declare const checkTagEmits: {
    'update:checked': (value: boolean) => boolean;
    change: (value: boolean) => boolean;
};
export type CheckTagEmits = typeof checkTagEmits;
export type CheckTagInstance = InstanceType<typeof CheckTag> & unknown;
