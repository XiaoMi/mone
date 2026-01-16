declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    container: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | HTMLElement | Window) | (() => string | HTMLElement | Window | null) | ((new (...args: any[]) => string | HTMLElement | Window) | (() => string | HTMLElement | Window | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    bound: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    duration: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    marker: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    type: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "default" | "underline") | (() => "default" | "underline") | ((new (...args: any[]) => "default" | "underline") | (() => "default" | "underline"))[], unknown, unknown, string, boolean>;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    selectScrollTop: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
}, {
    scrollTo: (href?: string) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    click: (e: MouseEvent, href?: string | undefined) => void;
    change: (href: string) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    container: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | HTMLElement | Window) | (() => string | HTMLElement | Window | null) | ((new (...args: any[]) => string | HTMLElement | Window) | (() => string | HTMLElement | Window | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    bound: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    duration: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    marker: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    type: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "default" | "underline") | (() => "default" | "underline") | ((new (...args: any[]) => "default" | "underline") | (() => "default" | "underline"))[], unknown, unknown, string, boolean>;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    selectScrollTop: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
}>> & {
    onChange?: ((href: string) => any) | undefined;
    onClick?: ((e: MouseEvent, href?: string | undefined) => any) | undefined;
}, {
    marker: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    direction: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown>;
    offset: number;
    type: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "default" | "underline") | (() => "default" | "underline") | ((new (...args: any[]) => "default" | "underline") | (() => "default" | "underline"))[], unknown, unknown>;
    duration: number;
    bound: number;
    selectScrollTop: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
