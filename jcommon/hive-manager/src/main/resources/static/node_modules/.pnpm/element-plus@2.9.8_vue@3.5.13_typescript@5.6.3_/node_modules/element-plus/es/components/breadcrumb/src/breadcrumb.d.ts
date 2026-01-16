import type { ExtractPropTypes } from 'vue';
export declare const breadcrumbProps: {
    readonly separator: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "/", boolean>;
    readonly separatorIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type BreadcrumbProps = ExtractPropTypes<typeof breadcrumbProps>;
