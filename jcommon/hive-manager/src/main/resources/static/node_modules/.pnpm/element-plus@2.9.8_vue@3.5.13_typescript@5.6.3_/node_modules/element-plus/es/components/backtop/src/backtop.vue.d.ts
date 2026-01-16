declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly visibilityHeight: {
        readonly type: NumberConstructor;
        readonly default: 200;
    };
    readonly target: {
        readonly type: StringConstructor;
        readonly default: "";
    };
    readonly right: {
        readonly type: NumberConstructor;
        readonly default: 40;
    };
    readonly bottom: {
        readonly type: NumberConstructor;
        readonly default: 40;
    };
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    click: (evt: MouseEvent) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly visibilityHeight: {
        readonly type: NumberConstructor;
        readonly default: 200;
    };
    readonly target: {
        readonly type: StringConstructor;
        readonly default: "";
    };
    readonly right: {
        readonly type: NumberConstructor;
        readonly default: 40;
    };
    readonly bottom: {
        readonly type: NumberConstructor;
        readonly default: 40;
    };
}>> & {
    onClick?: ((evt: MouseEvent) => any) | undefined;
}, {
    readonly bottom: number;
    readonly right: number;
    readonly target: string;
    readonly visibilityHeight: number;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
