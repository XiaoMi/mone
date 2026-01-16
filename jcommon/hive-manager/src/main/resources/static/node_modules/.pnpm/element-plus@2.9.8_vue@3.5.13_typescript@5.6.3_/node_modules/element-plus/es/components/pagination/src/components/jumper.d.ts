import type { ExtractPropTypes } from 'vue';
import type Jumper from './jumper.vue';
export declare const paginationJumperProps: {
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type PaginationJumperProps = ExtractPropTypes<typeof paginationJumperProps>;
export type PaginationJumperInstance = InstanceType<typeof Jumper> & unknown;
