import type { ExtractPropTypes } from 'vue';
import type Prev from './prev.vue';
export declare const paginationPrevProps: {
    readonly disabled: BooleanConstructor;
    readonly currentPage: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly prevText: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly prevIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const paginationPrevEmits: {
    click: (evt: MouseEvent) => boolean;
};
export type PaginationPrevProps = ExtractPropTypes<typeof paginationPrevProps>;
export type PrevInstance = InstanceType<typeof Prev> & unknown;
