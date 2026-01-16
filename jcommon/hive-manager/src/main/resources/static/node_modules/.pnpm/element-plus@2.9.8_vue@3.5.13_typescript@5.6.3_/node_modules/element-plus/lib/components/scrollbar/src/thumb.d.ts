import type { ExtractPropTypes } from 'vue';
import type Thumb from './thumb.vue';
export declare const thumbProps: {
    readonly vertical: BooleanConstructor;
    readonly size: StringConstructor;
    readonly move: NumberConstructor;
    readonly ratio: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly always: BooleanConstructor;
};
export type ThumbProps = ExtractPropTypes<typeof thumbProps>;
export type ThumbInstance = InstanceType<typeof Thumb> & unknown;
