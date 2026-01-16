import type { ExtractPropTypes } from 'vue';
import type { PosInfo } from './types';
export declare const maskProps: {
    zIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    visible: BooleanConstructor;
    fill: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    pos: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => PosInfo) | (() => PosInfo | null) | ((new (...args: any[]) => PosInfo) | (() => PosInfo | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    targetAreaClickable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
};
export type MaskProps = ExtractPropTypes<typeof maskProps>;
