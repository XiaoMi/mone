declare const _default: import("vue").DefineComponent<{
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
}, {
    ns: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    id: import("vue").Ref<string>;
    containerKls: import("vue").ComputedRef<string[]>;
    currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
    itemSelected: import("vue").ComputedRef<boolean>;
    isDisabled: import("vue").ComputedRef<boolean>;
    select: import("./type").SelectContext;
    visible: import("vue").Ref<boolean>;
    hover: import("vue").Ref<boolean>;
    states: {
        index: number;
        groupDisabled: boolean;
        visible: boolean;
        hover: boolean;
    };
    hoverItem: () => void;
    updateOption: (query: string) => void;
    selectOptionClick: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
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
}>>, {
    disabled: boolean;
    created: boolean;
}>;
export default _default;
