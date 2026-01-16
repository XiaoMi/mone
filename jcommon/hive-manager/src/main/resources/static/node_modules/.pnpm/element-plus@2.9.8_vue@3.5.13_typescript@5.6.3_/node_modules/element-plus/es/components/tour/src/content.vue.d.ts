declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, string, boolean>;
    reference: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown, null, boolean>;
    strategy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy))[], "fixed" | "absolute", unknown, string, boolean>;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    showArrow: BooleanConstructor;
    zIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    close: () => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, string, boolean>;
    reference: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown, null, boolean>;
    strategy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy))[], "fixed" | "absolute", unknown, string, boolean>;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    showArrow: BooleanConstructor;
    zIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
}>> & {
    onClose?: (() => any) | undefined;
}, {
    zIndex: number;
    offset: number;
    placement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown>;
    strategy: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/core").Strategy))[], "fixed" | "absolute", unknown>;
    reference: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown>;
    showArrow: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
