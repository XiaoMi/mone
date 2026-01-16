import type { TooltipInstance } from 'element-plus/es/components/tooltip';
import type { Placement } from 'element-plus/es/components/popper';
import type { PropType, WritableComputedRef } from 'vue';
import type { TableColumnCtx } from './table-column/defaults';
import type { Store } from './store';
declare const _default: import("vue").DefineComponent<{
    placement: {
        type: PropType<Placement>;
        default: string;
    };
    store: {
        type: PropType<Store<unknown>>;
    };
    column: {
        type: PropType<TableColumnCtx<unknown>>;
    };
    upDataColumn: {
        type: FunctionConstructor;
    };
    appendTo: {
        type: StringConstructor;
    };
}, {
    tooltipVisible: import("vue").Ref<boolean>;
    multiple: import("vue").ComputedRef<boolean>;
    filterClassName: import("vue").ComputedRef<string>;
    filteredValue: WritableComputedRef<unknown[]>;
    filterValue: WritableComputedRef<string>;
    filters: import("vue").ComputedRef<import("./table-column/defaults").Filters | undefined>;
    handleConfirm: () => void;
    handleReset: () => void;
    handleSelect: (_filterValue?: string) => void;
    isPropAbsent: (prop: unknown) => prop is null | undefined;
    isActive: (filter: any) => boolean;
    t: import("element-plus/es/hooks").Translator;
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
    showFilterPanel: (e: MouseEvent) => void;
    hideFilterPanel: () => void;
    popperPaneRef: import("vue").ComputedRef<HTMLElement | undefined>;
    tooltip: import("vue").Ref<TooltipInstance | null>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    placement: {
        type: PropType<Placement>;
        default: string;
    };
    store: {
        type: PropType<Store<unknown>>;
    };
    column: {
        type: PropType<TableColumnCtx<unknown>>;
    };
    upDataColumn: {
        type: FunctionConstructor;
    };
    appendTo: {
        type: StringConstructor;
    };
}>>, {
    placement: Placement;
}>;
export default _default;
