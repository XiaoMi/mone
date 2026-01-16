import type { ExtractPropTypes } from 'vue';
import type Popconfirm from './popconfirm.vue';
export declare const popconfirmProps: {
    readonly title: StringConstructor;
    readonly confirmButtonText: StringConstructor;
    readonly cancelButtonText: StringConstructor;
    readonly confirmButtonType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown, "primary", boolean>;
    readonly cancelButtonType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown, "text", boolean>;
    readonly icon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown, () => any, boolean>;
    readonly iconColor: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "#f90", boolean>;
    readonly hideIcon: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly hideAfter: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 200, boolean>;
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly persistent: BooleanConstructor;
    readonly width: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 150, boolean>;
};
export declare const popconfirmEmits: {
    /**
     * @description triggers when click confirm button
     */
    confirm: (e: MouseEvent) => boolean;
    /**
     * @description triggers when click cancel button
     */
    cancel: (e: MouseEvent) => boolean;
};
export type PopconfirmEmits = typeof popconfirmEmits;
export type PopconfirmProps = ExtractPropTypes<typeof popconfirmProps>;
export type PopconfirmInstance = InstanceType<typeof Popconfirm> & unknown;
