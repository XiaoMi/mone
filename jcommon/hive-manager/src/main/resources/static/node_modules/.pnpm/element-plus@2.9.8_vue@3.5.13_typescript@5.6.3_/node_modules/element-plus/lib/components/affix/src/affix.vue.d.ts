declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly zIndex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty) | ((new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty))[], unknown, unknown, 100, boolean>;
    readonly target: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly position: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "top", boolean>;
}, {
    /** @description update affix status */
    update: () => void;
    /** @description update rootRect info */
    updateRoot: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    scroll: (args_0: {
        scrollTop: number;
        fixed: boolean;
    }) => void;
    change: (fixed: boolean) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly zIndex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty) | ((new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty))[], unknown, unknown, 100, boolean>;
    readonly target: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly position: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "top", boolean>;
}>> & {
    onScroll?: ((args_0: {
        scrollTop: number;
        fixed: boolean;
    }) => any) | undefined;
    onChange?: ((fixed: boolean) => any) | undefined;
}, {
    readonly position: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "top" | "bottom", unknown>;
    readonly zIndex: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty) | ((new (...args: any[]) => number | "-moz-initial" | "inherit" | "initial" | "revert" | "unset" | "auto") | (() => import("csstype").ZIndexProperty))[], unknown, unknown>;
    readonly offset: number;
    readonly target: string;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;

type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
