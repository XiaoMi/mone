import type { ExtractPropTypes } from 'vue';
import type Bar from './bar.vue';
export declare const barProps: {
    readonly always: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly minSize: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type BarProps = ExtractPropTypes<typeof barProps>;
export type BarInstance = InstanceType<typeof Bar> & unknown;
