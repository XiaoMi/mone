import type { ExtractPropTypes } from 'vue';
import type { RouteLocationRaw } from 'vue-router';
export declare const breadcrumbItemProps: {
    readonly to: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => RouteLocationRaw & {}) | (() => RouteLocationRaw) | ((new (...args: any[]) => RouteLocationRaw & {}) | (() => RouteLocationRaw))[], unknown, unknown, "", boolean>;
    readonly replace: BooleanConstructor;
};
export type BreadcrumbItemProps = ExtractPropTypes<typeof breadcrumbItemProps>;
