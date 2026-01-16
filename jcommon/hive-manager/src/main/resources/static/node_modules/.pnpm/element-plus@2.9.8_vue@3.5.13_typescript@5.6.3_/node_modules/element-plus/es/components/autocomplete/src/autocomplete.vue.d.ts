import type { AutocompleteData } from './autocomplete';
import type { TooltipInstance } from 'element-plus/es/components/tooltip';
import type { InputInstance } from 'element-plus/es/components/input';
declare function __VLS_template(): {
    prepend?(_: {}): any;
    append?(_: {}): any;
    prefix?(_: {}): any;
    suffix?(_: {}): any;
    loading?(_: {}): any;
    default?(_: {
        item: Record<string, any>;
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly ariaLabel: StringConstructor;
    readonly valueKey: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "value", boolean>;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, "", boolean>;
    readonly debounce: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 300, boolean>;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], "top" | "bottom" | "top-start" | "top-end" | "bottom-start" | "bottom-end", unknown, "bottom-start", boolean>;
    readonly fetchSuggestions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => AutocompleteData | ((queryString: string, cb: import("./autocomplete").AutocompleteFetchSuggestionsCallback) => import("element-plus/es/utils").Awaitable<AutocompleteData> | void)) | (() => import("./autocomplete").AutocompleteFetchSuggestions) | ((new (...args: any[]) => AutocompleteData | ((queryString: string, cb: import("./autocomplete").AutocompleteFetchSuggestionsCallback) => import("element-plus/es/utils").Awaitable<AutocompleteData> | void)) | (() => import("./autocomplete").AutocompleteFetchSuggestions))[], unknown, unknown, () => void, boolean>;
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly triggerOnFocus: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly selectWhenUnmatched: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly hideLoading: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly highlightFirstItem: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly fitInputWidth: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly clearable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly disabled: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly name: StringConstructor;
}, {
    /** @description the index of the currently highlighted item */
    highlightedIndex: import("vue").Ref<number>;
    /** @description autocomplete whether activated */
    activated: import("vue").Ref<boolean>;
    /** @description remote search loading status */
    loading: import("vue").Ref<boolean>;
    /** @description el-input component instance */
    inputRef: import("vue").Ref<InputInstance | undefined>;
    /** @description el-tooltip component instance */
    popperRef: import("vue").Ref<TooltipInstance | undefined>;
    /** @description fetch suggestions result */
    suggestions: import("vue").Ref<Record<string, any>[]>;
    /** @description triggers when a suggestion is clicked */
    handleSelect: (item: any) => Promise<void>;
    /** @description handle keyboard enter event */
    handleKeyEnter: () => Promise<void>;
    /** @description focus the input element */
    focus: () => void;
    /** @description blur the input element */
    blur: () => void;
    /** @description close suggestion */
    close: () => void;
    /** @description highlight an item in a suggestion */
    highlight: (index: number) => void;
    /** @description loading suggestion list */
    getData: (queryString: string) => Promise<void>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    select: (item: Record<string, any>) => void;
    clear: () => void;
    "update:modelValue": (value: string) => void;
    change: (value: string) => void;
    input: (value: string) => void;
    blur: (evt: FocusEvent) => void;
    focus: (evt: FocusEvent) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly ariaLabel: StringConstructor;
    readonly valueKey: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "value", boolean>;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, "", boolean>;
    readonly debounce: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 300, boolean>;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], "top" | "bottom" | "top-start" | "top-end" | "bottom-start" | "bottom-end", unknown, "bottom-start", boolean>;
    readonly fetchSuggestions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => AutocompleteData | ((queryString: string, cb: import("./autocomplete").AutocompleteFetchSuggestionsCallback) => import("element-plus/es/utils").Awaitable<AutocompleteData> | void)) | (() => import("./autocomplete").AutocompleteFetchSuggestions) | ((new (...args: any[]) => AutocompleteData | ((queryString: string, cb: import("./autocomplete").AutocompleteFetchSuggestionsCallback) => import("element-plus/es/utils").Awaitable<AutocompleteData> | void)) | (() => import("./autocomplete").AutocompleteFetchSuggestions))[], unknown, unknown, () => void, boolean>;
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly triggerOnFocus: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly selectWhenUnmatched: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly hideLoading: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly highlightFirstItem: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly fitInputWidth: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly clearable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly disabled: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly name: StringConstructor;
}>> & {
    "onUpdate:modelValue"?: ((value: string) => any) | undefined;
    onChange?: ((value: string) => any) | undefined;
    onFocus?: ((evt: FocusEvent) => any) | undefined;
    onBlur?: ((evt: FocusEvent) => any) | undefined;
    onInput?: ((value: string) => any) | undefined;
    onSelect?: ((item: Record<string, any>) => any) | undefined;
    onClear?: (() => any) | undefined;
}, {
    readonly disabled: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly modelValue: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>;
    readonly placement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], "top" | "bottom" | "top-start" | "top-end" | "bottom-start" | "bottom-end", unknown>;
    readonly clearable: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly popperClass: string;
    readonly teleported: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly valueKey: string;
    readonly debounce: number;
    readonly fetchSuggestions: import("./autocomplete").AutocompleteFetchSuggestions;
    readonly triggerOnFocus: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly selectWhenUnmatched: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly hideLoading: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly highlightFirstItem: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly fitInputWidth: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
