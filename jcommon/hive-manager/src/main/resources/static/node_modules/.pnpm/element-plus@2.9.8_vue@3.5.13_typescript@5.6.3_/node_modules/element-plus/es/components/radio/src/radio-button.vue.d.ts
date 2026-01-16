declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
    readonly label: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly value: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly name: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
    readonly label: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly value: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    readonly name: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
}>>, {
    readonly label: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
    readonly disabled: boolean;
    readonly value: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
    readonly name: string;
    readonly modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
