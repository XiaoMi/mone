import type { ComputedRef, ExtractPropTypes, InjectionKey } from 'vue';
import type ColorPicker from './color-picker.vue';
export declare const colorPickerProps: {
    readonly ariaLabel: StringConstructor;
    readonly modelValue: StringConstructor;
    readonly id: StringConstructor;
    readonly showAlpha: BooleanConstructor;
    readonly colorFormat: StringConstructor;
    readonly disabled: BooleanConstructor;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly tabindex: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 0, boolean>;
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly predefine: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
};
export declare const colorPickerEmits: {
    "update:modelValue": (val: string | null) => boolean;
    change: (val: string | null) => boolean;
    activeChange: (val: string | null) => boolean;
    focus: (evt: FocusEvent) => boolean;
    blur: (evt: FocusEvent) => boolean;
};
export type ColorPickerProps = ExtractPropTypes<typeof colorPickerProps>;
export type ColorPickerEmits = typeof colorPickerEmits;
export type ColorPickerInstance = InstanceType<typeof ColorPicker> & unknown;
export interface ColorPickerContext {
    currentColor: ComputedRef<string>;
}
export declare const colorPickerContextKey: InjectionKey<ColorPickerContext>;
