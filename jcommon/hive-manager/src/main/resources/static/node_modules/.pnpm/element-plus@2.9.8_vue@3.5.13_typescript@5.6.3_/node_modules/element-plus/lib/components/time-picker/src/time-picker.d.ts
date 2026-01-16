declare const _default: import("vue").DefineComponent<{
    /**
     * @description whether to pick a time range
     */
    isRange: {
        type: BooleanConstructor;
        default: boolean;
    };
    showNow: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    ariaLabel: StringConstructor;
    emptyValues: ArrayConstructor;
    valueOnClear: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown, undefined, boolean>;
    disabledDate: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    cellClassName: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    shortcuts: import("element-plus/es/utils").EpPropFinalized<ArrayConstructor, unknown, unknown, () => never[], boolean>;
    arrowControl: BooleanConstructor;
    tabindex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown, 0, boolean>;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    unlinkPanels: BooleanConstructor;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], import("element-plus").Placement, unknown, "bottom", boolean>;
    fallbackPlacements: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").Placement[]) | (() => import("element-plus").Placement[]) | ((new (...args: any[]) => import("element-plus").Placement[]) | (() => import("element-plus").Placement[]))[], unknown, unknown, readonly ["bottom", "top", "right", "left"], boolean>;
    disabledHours: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabledMinutes: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabledSeconds: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    id: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    name: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    format: StringConstructor;
    valueFormat: StringConstructor;
    dateFormat: StringConstructor;
    timeFormat: StringConstructor;
    type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    clearable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    clearIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    editable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    prefixIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown, "", boolean>;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly: BooleanConstructor;
    disabled: BooleanConstructor;
    placeholder: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<import("element-plus").Options>) | (() => Partial<import("element-plus").Options>) | ((new (...args: any[]) => Partial<import("element-plus").Options>) | (() => Partial<import("element-plus").Options>))[], unknown, unknown, () => {}, boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType) | ((new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType))[], unknown, unknown, "", boolean>;
    rangeSeparator: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "-", boolean>;
    startPlaceholder: StringConstructor;
    endPlaceholder: StringConstructor;
    defaultValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    defaultTime: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, "update:modelValue"[], "update:modelValue", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    /**
     * @description whether to pick a time range
     */
    isRange: {
        type: BooleanConstructor;
        default: boolean;
    };
    showNow: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    ariaLabel: StringConstructor;
    emptyValues: ArrayConstructor;
    valueOnClear: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown, undefined, boolean>;
    disabledDate: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    cellClassName: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    shortcuts: import("element-plus/es/utils").EpPropFinalized<ArrayConstructor, unknown, unknown, () => never[], boolean>;
    arrowControl: BooleanConstructor;
    tabindex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown, 0, boolean>;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    unlinkPanels: BooleanConstructor;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], import("element-plus").Placement, unknown, "bottom", boolean>;
    fallbackPlacements: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").Placement[]) | (() => import("element-plus").Placement[]) | ((new (...args: any[]) => import("element-plus").Placement[]) | (() => import("element-plus").Placement[]))[], unknown, unknown, readonly ["bottom", "top", "right", "left"], boolean>;
    disabledHours: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabledMinutes: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabledSeconds: {
        readonly type: import("vue").PropType<import("./common/props").GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    id: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    name: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => import("./common/props").SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    format: StringConstructor;
    valueFormat: StringConstructor;
    dateFormat: StringConstructor;
    timeFormat: StringConstructor;
    type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    clearable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    clearIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    editable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    prefixIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown, "", boolean>;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly: BooleanConstructor;
    disabled: BooleanConstructor;
    placeholder: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<import("element-plus").Options>) | (() => Partial<import("element-plus").Options>) | ((new (...args: any[]) => Partial<import("element-plus").Options>) | (() => Partial<import("element-plus").Options>))[], unknown, unknown, () => {}, boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType) | ((new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType))[], unknown, unknown, "", boolean>;
    rangeSeparator: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "-", boolean>;
    startPlaceholder: StringConstructor;
    endPlaceholder: StringConstructor;
    defaultValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    defaultTime: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => import("./common/props").SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>> & {
    "onUpdate:modelValue"?: ((...args: any[]) => any) | undefined;
}, {
    disabled: boolean;
    type: string;
    placeholder: string;
    modelValue: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType) | ((new (...args: any[]) => string | number | string[] | Date | [import("./common/props").DateModelType, import("./common/props").DateModelType]) | (() => import("./common/props").ModelValueType))[], unknown, unknown>;
    placement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("element-plus").Placement))[], import("element-plus").Placement, unknown>;
    tabindex: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown>;
    valueOnClear: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown>;
    readonly: boolean;
    prefixIcon: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>;
    validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    clearable: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    fallbackPlacements: import("element-plus").Placement[];
    popperOptions: Partial<import("element-plus").Options>;
    popperClass: string;
    editable: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    rangeSeparator: string;
    shortcuts: unknown[];
    showNow: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    arrowControl: boolean;
    unlinkPanels: boolean;
    isRange: boolean;
}>;
export default _default;
