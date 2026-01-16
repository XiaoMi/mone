declare function __VLS_template(): Partial<Record<NonNullable<string | number>, (_: {}) => any>>;
declare const __VLS_component: import("vue").DefineComponent<{
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "HH:mm:ss", boolean>;
    readonly prefix: StringConstructor;
    readonly suffix: StringConstructor;
    readonly title: StringConstructor;
    readonly value: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs) | ((new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs))[], unknown, unknown, 0, boolean>;
    readonly valueStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, {
    /**
     * @description current display value
     */
    displayValue: import("vue").ComputedRef<string>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    change: (value: number) => void;
    finish: () => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "HH:mm:ss", boolean>;
    readonly prefix: StringConstructor;
    readonly suffix: StringConstructor;
    readonly title: StringConstructor;
    readonly value: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs) | ((new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs))[], unknown, unknown, 0, boolean>;
    readonly valueStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>> & {
    onChange?: ((value: number) => any) | undefined;
    onFinish?: (() => any) | undefined;
}, {
    readonly value: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs) | ((new (...args: any[]) => number | import("dayjs").Dayjs) | (() => number | import("dayjs").Dayjs))[], unknown, unknown>;
    readonly format: string;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
