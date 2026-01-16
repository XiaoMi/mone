declare const _default: import("vue").DefineComponent<{
    /**
     * @description name of the group
     */
    label: StringConstructor;
    /**
     * @description whether to disable all options in this group
     */
    disabled: BooleanConstructor;
}, {
    groupRef: import("vue").Ref<HTMLElement | undefined>;
    visible: import("vue").ComputedRef<boolean>;
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
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    /**
     * @description name of the group
     */
    label: StringConstructor;
    /**
     * @description whether to disable all options in this group
     */
    disabled: BooleanConstructor;
}>>, {
    disabled: boolean;
}>;
export default _default;
