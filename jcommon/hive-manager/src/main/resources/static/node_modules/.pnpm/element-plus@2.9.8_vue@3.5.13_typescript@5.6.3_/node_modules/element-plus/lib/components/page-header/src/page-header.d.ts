import type { ExtractPropTypes } from 'vue';
import type PageHeader from './page-header.vue';
export declare const pageHeaderProps: {
    readonly icon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown, () => any, boolean>;
    readonly title: StringConstructor;
    readonly content: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
};
export type PageHeaderProps = ExtractPropTypes<typeof pageHeaderProps>;
export declare const pageHeaderEmits: {
    back: () => boolean;
};
export type PageHeaderEmits = typeof pageHeaderEmits;
export type PageHeaderInstance = InstanceType<typeof PageHeader> & unknown;
