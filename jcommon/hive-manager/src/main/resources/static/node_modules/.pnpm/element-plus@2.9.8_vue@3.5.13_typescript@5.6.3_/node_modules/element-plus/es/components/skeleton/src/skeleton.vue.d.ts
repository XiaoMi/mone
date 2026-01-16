declare function __VLS_template(): {
    template?(_: {
        key: number;
    }): any;
    default?(_: {
        [x: string]: unknown;
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly animated: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly count: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly rows: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3, boolean>;
    readonly loading: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly throttle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => import("element-plus/es/hooks").ThrottleType) | ((new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => import("element-plus/es/hooks").ThrottleType))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, {
    /** @description loading state */
    uiLoading: import("vue").Ref<boolean>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly animated: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly count: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly rows: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3, boolean>;
    readonly loading: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly throttle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => import("element-plus/es/hooks").ThrottleType) | ((new (...args: any[]) => number | {
            leading?: number;
            trailing?: number;
            initVal?: boolean;
        }) | (() => import("element-plus/es/hooks").ThrottleType))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>>, {
    readonly loading: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly rows: number;
    readonly count: number;
    readonly animated: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
