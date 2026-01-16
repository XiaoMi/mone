import type { ExtractPropTypes } from 'vue';
import type { Arrayable } from 'element-plus/es/utils';
export type CollapseActiveName = string | number;
export type CollapseModelValue = Arrayable<CollapseActiveName>;
export declare const emitChangeFn: (value: CollapseModelValue) => value is string | number | CollapseActiveName[];
export declare const collapseProps: {
    readonly accordion: BooleanConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | CollapseActiveName[]) | (() => CollapseModelValue) | ((new (...args: any[]) => string | number | CollapseActiveName[]) | (() => CollapseModelValue))[], unknown, unknown, () => [], boolean>;
};
export type CollapseProps = ExtractPropTypes<typeof collapseProps>;
export declare const collapseEmits: {
    "update:modelValue": (value: CollapseModelValue) => value is string | number | CollapseActiveName[];
    change: (value: CollapseModelValue) => value is string | number | CollapseActiveName[];
};
export type CollapseEmits = typeof collapseEmits;
