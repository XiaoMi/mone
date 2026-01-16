import type { ExtractPropTypes } from 'vue';
import type { CollapseActiveName } from './collapse';
export declare const collapseItemProps: {
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly name: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => CollapseActiveName) | ((new (...args: any[]) => string | number) | (() => CollapseActiveName))[], unknown, unknown, undefined, boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
};
export type CollapseItemProps = ExtractPropTypes<typeof collapseItemProps>;
