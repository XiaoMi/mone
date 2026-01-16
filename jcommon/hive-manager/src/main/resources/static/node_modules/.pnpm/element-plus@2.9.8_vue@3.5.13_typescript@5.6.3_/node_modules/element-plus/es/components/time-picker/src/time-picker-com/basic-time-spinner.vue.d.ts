declare const _default: import("vue").DefineComponent<{
    readonly disabledHours: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledMinutes: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledSeconds: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly role: {
        readonly type: import("vue").PropType<string>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spinnerDate: {
        readonly type: import("vue").PropType<import("dayjs").Dayjs>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly showSeconds: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly arrowControl: BooleanConstructor;
    readonly amPmMode: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A") | ((new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A"))[], unknown, unknown, "", boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    change: (...args: any[]) => void;
    "select-range": (...args: any[]) => void;
    "set-option": (...args: any[]) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly disabledHours: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledMinutes: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledSeconds: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly role: {
        readonly type: import("vue").PropType<string>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spinnerDate: {
        readonly type: import("vue").PropType<import("dayjs").Dayjs>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly showSeconds: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly arrowControl: BooleanConstructor;
    readonly amPmMode: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A") | ((new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A"))[], unknown, unknown, "", boolean>;
}>> & {
    onChange?: ((...args: any[]) => any) | undefined;
    "onSelect-range"?: ((...args: any[]) => any) | undefined;
    "onSet-option"?: ((...args: any[]) => any) | undefined;
}, {
    readonly arrowControl: boolean;
    readonly showSeconds: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly amPmMode: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A") | ((new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A"))[], unknown, unknown>;
}>;
export default _default;
