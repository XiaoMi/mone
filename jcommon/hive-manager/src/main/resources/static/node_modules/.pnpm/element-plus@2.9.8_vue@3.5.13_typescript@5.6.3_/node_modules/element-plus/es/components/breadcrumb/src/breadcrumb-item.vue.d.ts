declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly to: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw) | ((new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw))[], unknown, unknown, "", boolean>;
    readonly replace: BooleanConstructor;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly to: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw) | ((new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw))[], unknown, unknown, "", boolean>;
    readonly replace: BooleanConstructor;
}>>, {
    readonly to: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw) | ((new (...args: any[]) => import("vue-router").RouteLocationRaw & {}) | (() => import("vue-router").RouteLocationRaw))[], unknown, unknown>;
    readonly replace: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
