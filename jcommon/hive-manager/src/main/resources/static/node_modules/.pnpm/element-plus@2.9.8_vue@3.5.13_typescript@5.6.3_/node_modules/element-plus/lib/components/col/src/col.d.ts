import type { ExtractPropTypes } from 'vue';
import type Col from './col.vue';
export type ColSizeObject = {
    span?: number;
    offset?: number;
    pull?: number;
    push?: number;
};
export type ColSize = number | ColSizeObject;
export declare const colProps: {
    readonly tag: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "div", boolean>;
    readonly span: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 24, boolean>;
    readonly offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly pull: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly push: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly xs: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | ColSizeObject) | (() => ColSize) | ((new (...args: any[]) => number | ColSizeObject) | (() => ColSize))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly sm: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | ColSizeObject) | (() => ColSize) | ((new (...args: any[]) => number | ColSizeObject) | (() => ColSize))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly md: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | ColSizeObject) | (() => ColSize) | ((new (...args: any[]) => number | ColSizeObject) | (() => ColSize))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly lg: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | ColSizeObject) | (() => ColSize) | ((new (...args: any[]) => number | ColSizeObject) | (() => ColSize))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly xl: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | ColSizeObject) | (() => ColSize) | ((new (...args: any[]) => number | ColSizeObject) | (() => ColSize))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
};
export type ColProps = ExtractPropTypes<typeof colProps>;
export type ColInstance = InstanceType<typeof Col> & unknown;
