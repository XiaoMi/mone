declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly space: import("element-plus/es/utils").EpPropFinalized<readonly [NumberConstructor, StringConstructor], unknown, unknown, "", boolean>;
    readonly active: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly alignCenter: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly simple: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly finishStatus: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown, "finish", boolean>;
    readonly processStatus: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown, "process", boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    change: (newVal: number, oldVal: number) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly space: import("element-plus/es/utils").EpPropFinalized<readonly [NumberConstructor, StringConstructor], unknown, unknown, "", boolean>;
    readonly active: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly alignCenter: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly simple: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly finishStatus: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown, "finish", boolean>;
    readonly processStatus: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown, "process", boolean>;
}>> & {
    onChange?: ((newVal: number, oldVal: number) => any) | undefined;
}, {
    readonly direction: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "horizontal" | "vertical", unknown>;
    readonly space: import("element-plus/es/utils").EpPropMergeType<readonly [NumberConstructor, StringConstructor], unknown, unknown>;
    readonly active: number;
    readonly finishStatus: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown>;
    readonly processStatus: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "wait" | "error" | "finish" | "success" | "process", unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
