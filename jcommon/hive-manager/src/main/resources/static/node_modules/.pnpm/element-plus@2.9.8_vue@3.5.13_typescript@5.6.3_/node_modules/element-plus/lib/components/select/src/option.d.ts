export declare const COMPONENT_NAME = "ElOption";
export declare const optionProps: {
    value: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    label: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    created: BooleanConstructor;
    disabled: BooleanConstructor;
};
