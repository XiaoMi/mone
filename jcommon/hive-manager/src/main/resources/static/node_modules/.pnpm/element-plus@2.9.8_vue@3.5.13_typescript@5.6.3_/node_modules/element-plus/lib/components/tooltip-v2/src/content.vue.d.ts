import type { CSSProperties } from 'vue';
declare function __VLS_template(): {
    default?(_: {
        contentStyle: CSSProperties;
        contentClass: string & (string | undefined)[];
    }): any;
    default?(_: {}): any;
    arrow?(_: {
        side: string;
        style: CSSProperties;
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    nowrap: BooleanConstructor;
    ariaLabel: StringConstructor;
    arrowPadding: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number) | (() => number) | ((new (...args: any[]) => number) | (() => number))[], unknown, unknown, 5, boolean>;
    effect: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect) | ((new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect))[], unknown, unknown, "light", boolean>;
    contentClass: StringConstructor;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, "bottom", boolean>;
    reference: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown, null, boolean>;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 8, boolean>;
    strategy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy))[], "fixed" | "absolute", unknown, "absolute", boolean>;
    showArrow: BooleanConstructor;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    nowrap: BooleanConstructor;
    ariaLabel: StringConstructor;
    arrowPadding: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number) | (() => number) | ((new (...args: any[]) => number) | (() => number))[], unknown, unknown, 5, boolean>;
    effect: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect) | ((new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect))[], unknown, unknown, "light", boolean>;
    contentClass: StringConstructor;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, "bottom", boolean>;
    reference: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown, null, boolean>;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 8, boolean>;
    strategy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy))[], "fixed" | "absolute", unknown, "absolute", boolean>;
    showArrow: BooleanConstructor;
}>>, {
    offset: number;
    nowrap: boolean;
    placement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/dom").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown>;
    strategy: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => import("@floating-ui/dom").Strategy))[], "fixed" | "absolute", unknown>;
    reference: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null) | ((new (...args: any[]) => HTMLElement | import("@floating-ui/dom").VirtualElement) | (() => HTMLElement | import("@floating-ui/dom").VirtualElement | null))[], unknown, unknown>;
    effect: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect) | ((new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect))[], unknown, unknown>;
    showArrow: boolean;
    arrowPadding: number;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
