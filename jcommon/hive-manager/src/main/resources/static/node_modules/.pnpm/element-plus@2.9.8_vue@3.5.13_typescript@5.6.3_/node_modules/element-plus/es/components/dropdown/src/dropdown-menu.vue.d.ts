declare const _default: import("vue").DefineComponent<{
    onKeydown: {
        readonly type: import("vue").PropType<(e: KeyboardEvent) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, {
    size: import("vue").ComputedRef<string> | undefined;
    rovingFocusGroupRootStyle: import("vue").Ref<import("vue").StyleValue>;
    tabIndex: import("vue").Ref<number>;
    dropdownKls: import("vue").ComputedRef<string[]>;
    role: import("vue").ComputedRef<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "dialog" | "menu" | "grid" | "listbox" | "tooltip" | "tree" | "group" | "navigation", unknown>>;
    triggerId: import("vue").ComputedRef<string>;
    dropdownListWrapperRef: (el: Element | import("vue").ComponentPublicInstance | null) => void;
    handleKeydown: (e: KeyboardEvent) => void;
    onBlur: (e: Event) => void;
    onFocus: (e: FocusEvent) => void;
    onMousedown: (e: Event) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    onKeydown: {
        readonly type: import("vue").PropType<(e: KeyboardEvent) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>>, {}>;
export default _default;
