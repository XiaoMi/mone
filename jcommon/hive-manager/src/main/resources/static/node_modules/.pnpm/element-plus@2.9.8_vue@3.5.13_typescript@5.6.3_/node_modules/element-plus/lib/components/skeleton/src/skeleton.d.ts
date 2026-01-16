import type Skeleton from './skeleton.vue';
import type { ExtractPropTypes } from 'vue';
import type { ThrottleType } from 'element-plus/es/hooks';
export declare const skeletonProps: {
    readonly animated: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly count: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly rows: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3, boolean>;
    readonly loading: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly throttle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => ThrottleType) | ((new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => ThrottleType))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type SkeletonProps = ExtractPropTypes<typeof skeletonProps>;
export type SkeletonInstance = InstanceType<typeof Skeleton> & unknown;
