declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly accordion: BooleanConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue) | ((new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue))[], unknown, unknown, () => [], boolean>;
}, {
    /** @description active names */
    activeNames: import("vue").Ref<(string | number)[]>;
    /** @description set active names */
    setActiveNames: (_activeNames: import("./collapse").CollapseActiveName[]) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: import("./collapse").CollapseModelValue) => void;
    change: (value: import("./collapse").CollapseModelValue) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly accordion: BooleanConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue) | ((new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue))[], unknown, unknown, () => [], boolean>;
}>> & {
    "onUpdate:modelValue"?: ((value: import("./collapse").CollapseModelValue) => any) | undefined;
    onChange?: ((value: import("./collapse").CollapseModelValue) => any) | undefined;
}, {
    readonly modelValue: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue) | ((new (...args: any[]) => string | number | import("./collapse").CollapseActiveName[]) | (() => import("./collapse").CollapseModelValue))[], unknown, unknown>;
    readonly accordion: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
