import type { ExtractPropTypes } from 'vue';
import type Next from './next.vue';
export declare const paginationNextProps: {
    readonly disabled: BooleanConstructor;
    readonly currentPage: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly pageCount: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 50, boolean>;
    readonly nextText: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly nextIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type PaginationNextProps = ExtractPropTypes<typeof paginationNextProps>;
export type NextInstance = InstanceType<typeof Next> & unknown;
