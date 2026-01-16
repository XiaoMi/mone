import type { default as CascaderNode } from './node';
import type { PropType } from 'vue';
declare const _default: import("vue").DefineComponent<{
    nodes: {
        type: PropType<CascaderNode[]>;
        required: true;
    };
    index: {
        type: NumberConstructor;
        required: true;
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
    panel: import("./types").ElCascaderPanelContext;
    hoverZone: import("vue").Ref<SVGSVGElement | null>;
    isEmpty: import("vue").ComputedRef<boolean>;
    isLoading: import("vue").ComputedRef<boolean>;
    menuId: import("vue").ComputedRef<string>;
    t: import("element-plus/es/hooks").Translator;
    handleExpand: (e: MouseEvent) => void;
    handleMouseMove: (e: MouseEvent) => void;
    clearHoverZone: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    nodes: {
        type: PropType<CascaderNode[]>;
        required: true;
    };
    index: {
        type: NumberConstructor;
        required: true;
    };
}>>, {}>;
export default _default;
