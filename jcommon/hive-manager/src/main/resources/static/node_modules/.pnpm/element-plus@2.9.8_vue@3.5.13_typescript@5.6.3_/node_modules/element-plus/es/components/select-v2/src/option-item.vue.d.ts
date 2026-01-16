declare const _default: import("vue").DefineComponent<{
    readonly data: ArrayConstructor;
    readonly disabled: BooleanConstructor;
    readonly hovering: BooleanConstructor;
    readonly item: {
        readonly type: import("vue").PropType<import("./select.types").Option>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly index: NumberConstructor;
    readonly style: ObjectConstructor;
    readonly selected: BooleanConstructor;
    readonly created: BooleanConstructor;
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
    hoverItem: () => void;
    selectOptionClick: () => void;
    getLabel: (option: import("./select.types").Option) => any;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    hover: (index?: number) => index is number;
    select: (val: import("./select.types").Option, index?: number) => boolean;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly data: ArrayConstructor;
    readonly disabled: BooleanConstructor;
    readonly hovering: BooleanConstructor;
    readonly item: {
        readonly type: import("vue").PropType<import("./select.types").Option>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly index: NumberConstructor;
    readonly style: ObjectConstructor;
    readonly selected: BooleanConstructor;
    readonly created: BooleanConstructor;
}>> & {
    onSelect?: ((val: import("./select.types").Option, index?: number | undefined) => any) | undefined;
    onHover?: ((index?: number | undefined) => any) | undefined;
}, {
    readonly disabled: boolean;
    readonly created: boolean;
    readonly selected: boolean;
    readonly hovering: boolean;
}>;
export default _default;
