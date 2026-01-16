import type { CSSProperties } from 'vue';
import type { InputInstance } from 'element-plus/es/components/input';
import type { TooltipInstance } from 'element-plus/es/components/tooltip';
import type { MentionOption } from './types';
declare function __VLS_template(): Partial<Record<NonNullable<string | number>, (_: any) => any>> & Partial<Record<NonNullable<string | number>, (_: any) => any>>;
declare const __VLS_component: import("vue").DefineComponent<{
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => MentionOption[]) | (() => MentionOption[]) | ((new (...args: any[]) => MentionOption[]) | (() => MentionOption[]))[], unknown, unknown, () => never[], boolean>;
    prefix: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | string[]) | (() => string | string[]) | ((new (...args: any[]) => string | string[]) | (() => string | string[]))[], unknown, unknown, string, boolean>;
    split: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    filterOption: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)) | ((new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)))[], unknown, unknown, () => (pattern: string, option: MentionOption) => boolean, boolean>;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom") | ((new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom"))[], unknown, unknown, string, boolean>;
    showArrow: BooleanConstructor;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    whole: BooleanConstructor;
    checkIsWhole: {
        readonly type: import("vue").PropType<(pattern: string, prefix: string) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    modelValue: StringConstructor;
    loading: BooleanConstructor;
    popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<import("@popperjs/core").Options>) | (() => Partial<import("@popperjs/core").Options>) | ((new (...args: any[]) => Partial<import("@popperjs/core").Options>) | (() => Partial<import("@popperjs/core").Options>))[], unknown, unknown, () => Partial<import("@popperjs/core").Options>, boolean>;
    ariaLabel: StringConstructor;
    id: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    maxlength: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    minlength: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "text", boolean>;
    resize: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "none" | "both" | "horizontal" | "vertical", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    autosize: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => boolean | {
        minRows?: number;
        maxRows?: number;
    }) | (() => import("element-plus/es/components/input").InputAutoSize) | ((new (...args: any[]) => boolean | {
        minRows?: number;
        maxRows?: number;
    }) | (() => import("element-plus/es/components/input").InputAutoSize))[], unknown, unknown, false, boolean>;
    autocomplete: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "off", boolean>;
    formatter: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    parser: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    placeholder: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    form: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly: BooleanConstructor;
    clearable: BooleanConstructor;
    showPassword: BooleanConstructor;
    showWordLimit: BooleanConstructor;
    suffixIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    prefixIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    containerRole: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    tabindex: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 0, boolean>;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    inputStyle: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    autofocus: BooleanConstructor;
    rows: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 2, boolean>;
}, {
    input: import("vue").Ref<InputInstance | undefined>;
    tooltip: import("vue").Ref<TooltipInstance | undefined>;
    dropdownVisible: import("vue").ComputedRef<boolean>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    search: (pattern: string, prefix: string) => void;
    select: (option: MentionOption, prefix: string) => void;
    "update:modelValue": (value: string) => void;
    blur: (evt: FocusEvent) => void;
    focus: (evt: FocusEvent) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => MentionOption[]) | (() => MentionOption[]) | ((new (...args: any[]) => MentionOption[]) | (() => MentionOption[]))[], unknown, unknown, () => never[], boolean>;
    prefix: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | string[]) | (() => string | string[]) | ((new (...args: any[]) => string | string[]) | (() => string | string[]))[], unknown, unknown, string, boolean>;
    split: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    filterOption: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)) | ((new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)))[], unknown, unknown, () => (pattern: string, option: MentionOption) => boolean, boolean>;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom") | ((new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom"))[], unknown, unknown, string, boolean>;
    showArrow: BooleanConstructor;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    whole: BooleanConstructor;
    checkIsWhole: {
        readonly type: import("vue").PropType<(pattern: string, prefix: string) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    modelValue: StringConstructor;
    loading: BooleanConstructor;
    popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<import("@popperjs/core").Options>) | (() => Partial<import("@popperjs/core").Options>) | ((new (...args: any[]) => Partial<import("@popperjs/core").Options>) | (() => Partial<import("@popperjs/core").Options>))[], unknown, unknown, () => Partial<import("@popperjs/core").Options>, boolean>;
    ariaLabel: StringConstructor;
    id: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    maxlength: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    minlength: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "text", boolean>;
    resize: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "none" | "both" | "horizontal" | "vertical", unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    autosize: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => boolean | {
        minRows?: number;
        maxRows?: number;
    }) | (() => import("element-plus/es/components/input").InputAutoSize) | ((new (...args: any[]) => boolean | {
        minRows?: number;
        maxRows?: number;
    }) | (() => import("element-plus/es/components/input").InputAutoSize))[], unknown, unknown, false, boolean>;
    autocomplete: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "off", boolean>;
    formatter: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    parser: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    placeholder: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    form: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly: BooleanConstructor;
    clearable: BooleanConstructor;
    showPassword: BooleanConstructor;
    showWordLimit: BooleanConstructor;
    suffixIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    prefixIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    containerRole: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    tabindex: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 0, boolean>;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    inputStyle: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    autofocus: BooleanConstructor;
    rows: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 2, boolean>;
}>> & {
    "onUpdate:modelValue"?: ((value: string) => any) | undefined;
    onFocus?: ((evt: FocusEvent) => any) | undefined;
    onBlur?: ((evt: FocusEvent) => any) | undefined;
    onSelect?: ((option: MentionOption, prefix: string) => any) | undefined;
    onSearch?: ((pattern: string, prefix: string) => any) | undefined;
}, {
    disabled: boolean;
    offset: number;
    id: string;
    type: string;
    split: string;
    loading: boolean;
    placement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom") | ((new (...args: any[]) => "top" | "bottom") | (() => "top" | "bottom"))[], unknown, unknown>;
    options: MentionOption[];
    tabindex: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>;
    readonly: boolean;
    autosize: import("element-plus/es/components/input").InputAutoSize;
    autocomplete: string;
    containerRole: string;
    validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    inputStyle: import("vue").StyleValue;
    rows: number;
    clearable: boolean;
    showPassword: boolean;
    showWordLimit: boolean;
    autofocus: boolean;
    prefix: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | string[]) | (() => string | string[]) | ((new (...args: any[]) => string | string[]) | (() => string | string[]))[], unknown, unknown>;
    popperOptions: Partial<import("@popperjs/core").Options>;
    popperClass: string;
    showArrow: boolean;
    filterOption: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)) | ((new (...args: any[]) => false | ((pattern: string, option: MentionOption) => boolean)) | (() => false | ((pattern: string, option: MentionOption) => boolean)))[], unknown, unknown>;
    whole: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
