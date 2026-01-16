import type { Component, ExtractPropTypes } from 'vue';
import type Result from './result.vue';
export declare const IconMap: {
    readonly success: "icon-success";
    readonly warning: "icon-warning";
    readonly error: "icon-error";
    readonly info: "icon-info";
};
export declare const IconComponentMap: Record<typeof IconMap[keyof typeof IconMap], Component>;
export declare const resultProps: {
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly subTitle: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly icon: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "error" | "success" | "warning" | "info", unknown, "info", boolean>;
};
export type ResultProps = ExtractPropTypes<typeof resultProps>;
export type ResultInstance = InstanceType<typeof Result> & unknown;
