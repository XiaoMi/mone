declare const _default: import("vue").DefineComponent<{
    readonly command: import("element-plus/es/utils").EpPropFinalized<readonly [ObjectConstructor, StringConstructor, NumberConstructor], unknown, unknown, () => {}, boolean>;
    readonly disabled: BooleanConstructor;
    readonly divided: BooleanConstructor;
    readonly textValue: StringConstructor;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
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
    itemRef: (el: Element | import("vue").ComponentPublicInstance | null) => void;
    dataset: {
        "data-el-collection-item": string;
    };
    role: import("vue").ComputedRef<string>;
    tabIndex: import("vue").Ref<number>;
    handleFocus: (e: Event) => void;
    handleKeydown: (event: KeyboardEvent) => void;
    handleMousedown: (e: Event) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("click" | "pointerleave" | "pointermove" | "clickimpl")[], "click" | "pointerleave" | "pointermove" | "clickimpl", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly command: import("element-plus/es/utils").EpPropFinalized<readonly [ObjectConstructor, StringConstructor, NumberConstructor], unknown, unknown, () => {}, boolean>;
    readonly disabled: BooleanConstructor;
    readonly divided: BooleanConstructor;
    readonly textValue: StringConstructor;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>> & {
    onClick?: ((...args: any[]) => any) | undefined;
    onPointermove?: ((...args: any[]) => any) | undefined;
    onPointerleave?: ((...args: any[]) => any) | undefined;
    onClickimpl?: ((...args: any[]) => any) | undefined;
}, {
    readonly disabled: boolean;
    readonly command: import("element-plus/es/utils").EpPropMergeType<readonly [ObjectConstructor, StringConstructor, NumberConstructor], unknown, unknown>;
    readonly divided: boolean;
}>;
export default _default;
