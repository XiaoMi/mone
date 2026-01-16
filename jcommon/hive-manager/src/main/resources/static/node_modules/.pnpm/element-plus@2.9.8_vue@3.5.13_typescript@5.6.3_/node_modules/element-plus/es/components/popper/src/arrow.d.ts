import type { ExtractPropTypes } from 'vue';
import type Arrow from './arrow.vue';
export declare const popperArrowProps: {
    readonly arrowOffset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 5, boolean>;
};
export type PopperArrowProps = ExtractPropTypes<typeof popperArrowProps>;
export type PopperArrowInstance = InstanceType<typeof Arrow> & unknown;
/** @deprecated use `popperArrowProps` instead, and it will be deprecated in the next major version */
export declare const usePopperArrowProps: {
    readonly arrowOffset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 5, boolean>;
};
/** @deprecated use `PopperArrowProps` instead, and it will be deprecated in the next major version */
export type UsePopperArrowProps = PopperArrowProps;
/** @deprecated use `PopperArrowInstance` instead, and it will be deprecated in the next major version */
export type ElPopperArrowInstance = PopperArrowInstance;
