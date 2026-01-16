import type { Option } from './types';
import type { ExtractPropTypes } from 'vue';
import type Segmented from './segmented.vue';
export interface Props {
    label?: string;
    value?: string;
    disabled?: string;
}
export declare const defaultProps: Required<Props>;
export declare const segmentedProps: {
    ariaLabel: StringConstructor;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Option[]) | (() => Option[]) | ((new (...args: any[]) => Option[]) | (() => Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Props) | (() => Props) | ((new (...args: any[]) => Props) | (() => Props))[], unknown, unknown, () => Required<Props>, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
};
export type SegmentedProps = ExtractPropTypes<typeof segmentedProps>;
export declare const segmentedEmits: {
    "update:modelValue": (val: any) => val is string | number | boolean;
    change: (val: any) => val is string | number | boolean;
};
export type SegmentedEmits = typeof segmentedEmits;
export type SegmentedInstance = InstanceType<typeof Segmented> & unknown;
