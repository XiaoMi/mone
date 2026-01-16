import type { ComponentInternalInstance, PropType, Ref } from 'vue';
import type { DefaultRow, Sort } from '../table/defaults';
import type { Store } from '../store';
export interface TableHeader extends ComponentInternalInstance {
    state: {
        onColumnsChange: any;
        onScrollableChange: any;
    };
    filterPanels: Ref<unknown>;
}
export interface TableHeaderProps<T> {
    fixed: string;
    store: Store<T>;
    border: boolean;
    defaultSort: Sort;
    allowDragLastColumn: boolean;
}
declare const _default: import("vue").DefineComponent<{
    fixed: {
        type: StringConstructor;
        default: string;
    };
    store: {
        required: true;
        type: PropType<TableHeaderProps<DefaultRow>["store"]>;
    };
    border: BooleanConstructor;
    defaultSort: {
        type: PropType<TableHeaderProps<DefaultRow>["defaultSort"]>;
        default: () => {
            prop: string;
            order: string;
        };
    };
    appendFilterPanelTo: {
        type: StringConstructor;
    };
    allowDragLastColumn: {
        type: BooleanConstructor;
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
    filterPanels: Ref<{}>;
    onColumnsChange: (layout: import("../table-layout.js").default<any>) => void;
    onScrollableChange: (layout: import("../table-layout.js").default<any>) => void;
    columnRows: import("vue").ComputedRef<import("../table-column/defaults.js").TableColumnCtx<unknown>[]>;
    getHeaderRowClass: (rowIndex: number) => string;
    getHeaderRowStyle: (rowIndex: number) => any;
    getHeaderCellClass: (rowIndex: number, columnIndex: number, row: unknown, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => string;
    getHeaderCellStyle: (rowIndex: number, columnIndex: number, row: unknown, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => any;
    handleHeaderClick: (event: Event, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => void;
    handleHeaderContextMenu: (event: Event, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => void;
    handleMouseDown: (event: MouseEvent, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => void;
    handleMouseMove: (event: MouseEvent, column: import("../table-column/defaults.js").TableColumnCtx<unknown>) => void;
    handleMouseOut: () => void;
    handleSortClick: (event: Event, column: import("../table-column/defaults.js").TableColumnCtx<unknown>, givenOrder: string | boolean) => void;
    handleFilterClick: (event: Event) => void;
    isGroup: import("vue").ComputedRef<boolean>;
    toggleAllSelection: (event: Event) => void;
    saveIndexSelection: Map<any, any>;
    isTableLayoutAuto: boolean;
    theadRef: Ref<any>;
    updateFixedColumnStyle: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    fixed: {
        type: StringConstructor;
        default: string;
    };
    store: {
        required: true;
        type: PropType<TableHeaderProps<DefaultRow>["store"]>;
    };
    border: BooleanConstructor;
    defaultSort: {
        type: PropType<TableHeaderProps<DefaultRow>["defaultSort"]>;
        default: () => {
            prop: string;
            order: string;
        };
    };
    appendFilterPanelTo: {
        type: StringConstructor;
    };
    allowDragLastColumn: {
        type: BooleanConstructor;
    };
}>>, {
    fixed: string;
    border: boolean;
    defaultSort: Sort;
    allowDragLastColumn: boolean;
}>;
export default _default;
