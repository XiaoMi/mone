declare function __VLS_template(): {
    prefix?(_: {}): any;
    "range-separator"?(_: {}): any;
    suffix?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly id: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly name: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string | null, string | null]) | (() => import("./props").UserInput) | ((new (...args: any[]) => string | [string | null, string | null]) | (() => import("./props").UserInput))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly startPlaceholder: StringConstructor;
    readonly endPlaceholder: StringConstructor;
    readonly disabled: BooleanConstructor;
}, {
    focus: () => void;
    blur: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    click: (...args: any[]) => void;
    blur: (...args: any[]) => void;
    focus: (...args: any[]) => void;
    mouseenter: (...args: any[]) => void;
    mouseleave: (...args: any[]) => void;
    touchstart: (...args: any[]) => void;
    startInput: (...args: any[]) => void;
    endInput: (...args: any[]) => void;
    startChange: (...args: any[]) => void;
    endChange: (...args: any[]) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly id: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly name: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string | null, string | null]) | (() => import("./props").UserInput) | ((new (...args: any[]) => string | [string | null, string | null]) | (() => import("./props").UserInput))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly startPlaceholder: StringConstructor;
    readonly endPlaceholder: StringConstructor;
    readonly disabled: BooleanConstructor;
}>> & {
    onFocus?: ((...args: any[]) => any) | undefined;
    onBlur?: ((...args: any[]) => any) | undefined;
    onClick?: ((...args: any[]) => any) | undefined;
    onMouseenter?: ((...args: any[]) => any) | undefined;
    onMouseleave?: ((...args: any[]) => any) | undefined;
    onTouchstart?: ((...args: any[]) => any) | undefined;
    onStartInput?: ((...args: any[]) => any) | undefined;
    onEndInput?: ((...args: any[]) => any) | undefined;
    onStartChange?: ((...args: any[]) => any) | undefined;
    onEndChange?: ((...args: any[]) => any) | undefined;
}, {
    readonly disabled: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
