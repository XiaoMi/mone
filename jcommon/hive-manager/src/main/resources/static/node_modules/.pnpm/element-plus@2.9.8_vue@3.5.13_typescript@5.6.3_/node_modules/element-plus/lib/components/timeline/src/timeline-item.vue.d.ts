declare function __VLS_template(): {
    dot?(_: {}): any;
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly timestamp: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly hideTimestamp: BooleanConstructor;
    readonly center: BooleanConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "bottom", boolean>;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
    readonly color: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly size: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "normal" | "large", unknown, "normal", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly hollow: BooleanConstructor;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly timestamp: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly hideTimestamp: BooleanConstructor;
    readonly center: BooleanConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "bottom", boolean>;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
    readonly color: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly size: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "normal" | "large", unknown, "normal", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly hollow: BooleanConstructor;
}>>, {
    readonly center: boolean;
    readonly color: string;
    readonly size: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "normal" | "large", unknown>;
    readonly type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown>;
    readonly placement: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "top" | "bottom", unknown>;
    readonly timestamp: string;
    readonly hideTimestamp: boolean;
    readonly hollow: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
