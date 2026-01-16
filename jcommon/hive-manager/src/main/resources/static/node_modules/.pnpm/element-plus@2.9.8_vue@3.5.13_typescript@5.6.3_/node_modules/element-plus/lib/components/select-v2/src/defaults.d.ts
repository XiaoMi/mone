import type { Option, OptionType } from './select.types';
import type { Props } from './useProps';
import type { EmitFn } from 'element-plus/es/utils/vue/typescript';
import type { ExtractPropTypes } from 'vue';
import type { Options, Placement, PopperEffect } from 'element-plus/es/components/popper';
export declare const SelectProps: {
    readonly ariaLabel: StringConstructor;
    readonly emptyValues: ArrayConstructor;
    readonly valueOnClear: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown, undefined, boolean>;
    readonly allowCreate: BooleanConstructor;
    readonly autocomplete: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "none" | "both" | "inline" | "list") | (() => "none" | "both" | "inline" | "list") | ((new (...args: any[]) => "none" | "both" | "inline" | "list") | (() => "none" | "both" | "inline" | "list"))[], unknown, unknown, "none", boolean>;
    readonly automaticDropdown: BooleanConstructor;
    readonly clearable: BooleanConstructor;
    readonly clearIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly effect: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => PopperEffect) | ((new (...args: any[]) => string) | (() => PopperEffect))[], unknown, unknown, "light", boolean>;
    readonly collapseTags: BooleanConstructor;
    readonly collapseTagsTooltip: BooleanConstructor;
    readonly maxCollapseTags: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly defaultFirstOption: BooleanConstructor;
    readonly disabled: BooleanConstructor;
    readonly estimatedOptionHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, undefined, boolean>;
    readonly filterable: BooleanConstructor;
    readonly filterMethod: FunctionConstructor;
    readonly height: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 274, boolean>;
    readonly itemHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 34, boolean>;
    readonly id: StringConstructor;
    readonly loading: BooleanConstructor;
    readonly loadingText: StringConstructor;
    readonly modelValue: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly multiple: BooleanConstructor;
    readonly multipleLimit: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly name: StringConstructor;
    readonly noDataText: StringConstructor;
    readonly noMatchText: StringConstructor;
    readonly remoteMethod: FunctionConstructor;
    readonly reserveKeyword: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly options: {
        readonly type: import("vue").PropType<OptionType[]>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly placeholder: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly persistent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<Options>) | (() => Partial<Options>) | ((new (...args: any[]) => Partial<Options>) | (() => Partial<Options>))[], unknown, unknown, () => Partial<Options>, boolean>;
    readonly remote: BooleanConstructor;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Props) | (() => Props) | ((new (...args: any[]) => Props) | (() => Props))[], unknown, unknown, () => Required<Props>, boolean>;
    readonly valueKey: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "value", boolean>;
    readonly scrollbarAlwaysOn: BooleanConstructor;
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 12, boolean>;
    readonly showArrow: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement))[], Placement, unknown, "bottom-start", boolean>;
    readonly fallbackPlacements: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Placement[]) | (() => Placement[]) | ((new (...args: any[]) => Placement[]) | (() => Placement[]))[], unknown, unknown, readonly ["bottom-start", "top-start", "right", "left"], boolean>;
    readonly tagType: {
        readonly default: "info";
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        readonly __epPropKey: true;
    };
    readonly tagEffect: {
        readonly default: "light";
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "dark" | "light" | "plain", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        readonly __epPropKey: true;
    };
    readonly tabindex: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 0, boolean>;
    readonly appendTo: StringConstructor;
    readonly fitInputWidth: import("element-plus/es/utils").EpPropFinalized<readonly [BooleanConstructor, NumberConstructor], unknown, number | boolean, true, boolean>;
    readonly suffixIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const OptionProps: {
    readonly data: ArrayConstructor;
    readonly disabled: BooleanConstructor;
    readonly hovering: BooleanConstructor;
    readonly item: {
        readonly type: import("vue").PropType<Option>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly index: NumberConstructor;
    readonly style: ObjectConstructor;
    readonly selected: BooleanConstructor;
    readonly created: BooleanConstructor;
};
export declare const selectEmits: {
    "update:modelValue": (val: ISelectV2Props["modelValue"]) => boolean;
    change: (val: ISelectV2Props["modelValue"]) => boolean;
    'remove-tag': (val: unknown) => boolean;
    'visible-change': (visible: boolean) => boolean;
    focus: (evt: FocusEvent) => boolean;
    blur: (evt: FocusEvent) => boolean;
    clear: () => boolean;
};
export declare const optionEmits: {
    hover: (index?: number) => index is number;
    select: (val: Option, index?: number) => boolean;
};
export type ISelectV2Props = ExtractPropTypes<typeof SelectProps>;
export type IOptionV2Props = ExtractPropTypes<typeof OptionProps>;
export type SelectEmitFn = EmitFn<typeof selectEmits>;
export type OptionEmitFn = EmitFn<typeof optionEmits>;
