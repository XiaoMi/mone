import type Total from './total.vue';
import type { ExtractPropTypes } from 'vue';
export declare const paginationTotalProps: {
    readonly total: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1000, boolean>;
};
export type PaginationTotalProps = ExtractPropTypes<typeof paginationTotalProps>;
export type TotalInstance = InstanceType<typeof Total> & unknown;
