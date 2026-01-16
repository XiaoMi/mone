import dayjs from 'dayjs';
declare const _default: import("vue").DefineComponent<{
    readonly parsedValue: {
        readonly type: import("vue").PropType<[dayjs.Dayjs, dayjs.Dayjs]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly visible: BooleanConstructor;
    readonly actualVisible: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "select-range": (...args: any[]) => void;
    pick: (...args: any[]) => void;
    "set-picker-option": (...args: any[]) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly parsedValue: {
        readonly type: import("vue").PropType<[dayjs.Dayjs, dayjs.Dayjs]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly visible: BooleanConstructor;
    readonly actualVisible: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
}>> & {
    onPick?: ((...args: any[]) => any) | undefined;
    "onSelect-range"?: ((...args: any[]) => any) | undefined;
    "onSet-picker-option"?: ((...args: any[]) => any) | undefined;
}, {
    readonly visible: boolean;
    readonly actualVisible: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly format: string;
}>;
export default _default;
