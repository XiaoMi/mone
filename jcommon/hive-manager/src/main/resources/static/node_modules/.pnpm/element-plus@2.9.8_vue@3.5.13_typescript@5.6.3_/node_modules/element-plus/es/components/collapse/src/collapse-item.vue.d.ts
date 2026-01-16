declare function __VLS_template(): {
    title?(_: {}): any;
    icon?(_: {
        isActive: boolean | undefined;
    }): any;
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly name: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName) | ((new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName))[], unknown, unknown, undefined, boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
}, {
    /** @description current collapse-item whether active */
    isActive: import("vue").ComputedRef<boolean | undefined>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly name: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName) | ((new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName))[], unknown, unknown, undefined, boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
}>>, {
    readonly title: string;
    readonly disabled: boolean;
    readonly name: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName) | ((new (...args: any[]) => string | number) | (() => import("element-plus").CollapseActiveName))[], unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
