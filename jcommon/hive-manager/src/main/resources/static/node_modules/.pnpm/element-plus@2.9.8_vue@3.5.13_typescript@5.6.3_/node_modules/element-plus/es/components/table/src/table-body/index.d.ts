import type { VNode } from 'vue';
declare const _default: import("vue").DefineComponent<{
    store: {
        required: boolean;
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["store"]>;
    };
    stripe: BooleanConstructor;
    tooltipEffect: StringConstructor;
    tooltipOptions: {
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["tooltipOptions"]>;
    };
    context: {
        default: () => {};
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["context"]>;
    };
    rowClassName: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["rowClassName"]>;
    rowStyle: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["rowStyle"]>;
    fixed: {
        type: StringConstructor;
        default: string;
    };
    highlight: BooleanConstructor;
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
    onColumnsChange: (layout: import("../table-layout").default<any>) => void;
    onScrollableChange: (layout: import("../table-layout").default<any>) => void;
    wrappedRowRender: (row: any, $index: number) => VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }> | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>[] | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>[][];
    tooltipContent: import("vue").Ref<string>;
    tooltipTrigger: import("vue").Ref<VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    store: {
        required: boolean;
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["store"]>;
    };
    stripe: BooleanConstructor;
    tooltipEffect: StringConstructor;
    tooltipOptions: {
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["tooltipOptions"]>;
    };
    context: {
        default: () => {};
        type: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["context"]>;
    };
    rowClassName: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["rowClassName"]>;
    rowStyle: import("vue").PropType<import("./defaults").TableBodyProps<import("../table/defaults").DefaultRow>["rowStyle"]>;
    fixed: {
        type: StringConstructor;
        default: string;
    };
    highlight: BooleanConstructor;
}>>, {
    fixed: string;
    highlight: boolean;
    context: import("element-plus").Table<any>;
    stripe: boolean;
}>;
export default _default;
