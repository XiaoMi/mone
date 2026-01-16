import TableLayout from './table-layout';
import type { Table } from './table/defaults';
declare const _default: import("vue").DefineComponent<{
    data: {
        type: import("vue").PropType<import("./table/defaults").DefaultRow[]>;
        default: () => never[];
    };
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    width: (NumberConstructor | StringConstructor)[];
    height: (NumberConstructor | StringConstructor)[];
    maxHeight: (NumberConstructor | StringConstructor)[];
    fit: {
        type: BooleanConstructor;
        default: boolean;
    };
    stripe: BooleanConstructor;
    border: BooleanConstructor;
    rowKey: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowKey"]>;
    showHeader: {
        type: BooleanConstructor;
        default: boolean;
    };
    showSummary: BooleanConstructor;
    sumText: StringConstructor;
    summaryMethod: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["summaryMethod"]>;
    rowClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowClassName"]>;
    rowStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowStyle"]>;
    cellClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["cellClassName"]>;
    cellStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["cellStyle"]>;
    headerRowClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerRowClassName"]>;
    headerRowStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerRowStyle"]>;
    headerCellClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerCellClassName"]>;
    headerCellStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerCellStyle"]>;
    highlightCurrentRow: BooleanConstructor;
    currentRowKey: (NumberConstructor | StringConstructor)[];
    emptyText: StringConstructor;
    expandRowKeys: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["expandRowKeys"]>;
    defaultExpandAll: BooleanConstructor;
    defaultSort: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["defaultSort"]>;
    tooltipEffect: StringConstructor;
    tooltipOptions: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["tooltipOptions"]>;
    spanMethod: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["spanMethod"]>;
    selectOnIndeterminate: {
        type: BooleanConstructor;
        default: boolean;
    };
    indent: {
        type: NumberConstructor;
        default: number;
    };
    treeProps: {
        type: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["treeProps"]>;
        default: () => {
            hasChildren: string;
            children: string;
            checkStrictly: boolean;
        };
    };
    lazy: BooleanConstructor;
    load: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["load"]>;
    style: {
        type: import("vue").PropType<import("vue").CSSProperties>;
        default: () => {};
    };
    className: {
        type: StringConstructor;
        default: string;
    };
    tableLayout: {
        type: import("vue").PropType<"fixed" | "auto">;
        default: string;
    };
    scrollbarAlwaysOn: BooleanConstructor;
    flexible: BooleanConstructor;
    showOverflowTooltip: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["showOverflowTooltip"]>;
    tooltipFormatter: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["tooltipFormatter"]>;
    appendFilterPanelTo: StringConstructor;
    scrollbarTabindex: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    allowDragLastColumn: {
        type: BooleanConstructor;
        default: boolean;
    };
    preserveExpandedContent: {
        type: BooleanConstructor;
        default: boolean;
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
    layout: TableLayout<any>;
    store: any;
    columns: import("vue").ComputedRef<import("./table-column/defaults").TableColumnCtx<unknown>>;
    handleHeaderFooterMousewheel: (event: any, data: any) => void;
    handleMouseLeave: () => void;
    tableId: string;
    tableSize: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    isHidden: import("vue").Ref<boolean>;
    isEmpty: import("vue").ComputedRef<boolean>;
    renderExpanded: import("vue").Ref<null>;
    resizeProxyVisible: import("vue").Ref<boolean>;
    resizeState: import("vue").Ref<{
        width: null | number;
        height: null | number;
        headerHeight: null | number;
    }>;
    isGroup: import("vue").Ref<boolean>;
    bodyWidth: import("vue").ComputedRef<string>;
    tableBodyStyles: import("vue").ComputedRef<{
        width: string;
    }>;
    emptyBlockStyle: import("vue").ComputedRef<{
        width: string;
        height: string;
    } | null>;
    debouncedUpdateLayout: import("lodash").DebouncedFunc<() => void>;
    handleFixedMousewheel: (event: any, data: any) => void;
    /**
     * @description used in single selection Table, set a certain row selected. If called without any parameter, it will clear selection
     */
    setCurrentRow: (row: any) => void;
    /**
     * @description returns the currently selected rows
     */
    getSelectionRows: () => any;
    /**
     * @description used in multiple selection Table, toggle if a certain row is selected. With the second parameter, you can directly set if this row is selected
     */
    toggleRowSelection: (row: any, selected?: boolean, ignoreSelectable?: boolean) => void;
    /**
     * @description used in multiple selection Table, clear user selection
     */
    clearSelection: () => void;
    /**
     * @description clear filters of the columns whose `columnKey` are passed in. If no params, clear all filters
     */
    clearFilter: (columnKeys?: string[]) => void;
    /**
     * @description used in multiple selection Table, toggle select all and deselect all
     */
    toggleAllSelection: () => void;
    /**
     * @description used in expandable Table or tree Table, toggle if a certain row is expanded. With the second parameter, you can directly set if this row is expanded or collapsed
     */
    toggleRowExpansion: (row: any, expanded?: boolean) => void;
    /**
     * @description clear sorting, restore data to the original order
     */
    clearSort: () => void;
    /**
     * @description refresh the layout of Table. When the visibility of Table changes, you may need to call this method to get a correct layout
     */
    doLayout: () => void;
    /**
     * @description sort Table manually. Property `prop` is used to set sort column, property `order` is used to set sort order
     */
    sort: (prop: string, order: string) => void;
    /**
     * @description used in lazy Table, must set `rowKey`, update key children
     */
    updateKeyChildren: (key: string, data: any[]) => void;
    t: import("element-plus/es/hooks").Translator;
    setDragVisible: (visible: boolean) => void;
    context: Table<any>;
    computedSumText: import("vue").ComputedRef<string>;
    computedEmptyText: import("vue").ComputedRef<string>;
    tableLayout: import("vue").ComputedRef<("fixed" | "auto") | undefined>;
    scrollbarViewStyle: {
        display: string;
        verticalAlign: string;
    };
    scrollbarStyle: import("vue").ComputedRef<{
        height: string;
        maxHeight?: undefined;
    } | {
        maxHeight: string;
        height?: undefined;
    } | {
        height?: undefined;
        maxHeight?: undefined;
    }>;
    scrollBarRef: import("vue").Ref<any>;
    /**
     * @description scrolls to a particular set of coordinates
     */
    scrollTo: (options: ScrollToOptions | number, yCoord?: number) => void;
    /**
     * @description set horizontal scroll position
     */
    setScrollLeft: (left?: number) => void;
    /**
     * @description set vertical scroll position
     */
    setScrollTop: (top?: number) => void;
    /**
     * @description whether to allow drag the last column
     */
    allowDragLastColumn: boolean;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("select" | "scroll" | "select-all" | "expand-change" | "current-change" | "selection-change" | "cell-mouse-enter" | "cell-mouse-leave" | "cell-contextmenu" | "cell-click" | "cell-dblclick" | "row-click" | "row-contextmenu" | "row-dblclick" | "header-click" | "header-contextmenu" | "sort-change" | "filter-change" | "header-dragend")[], "select" | "scroll" | "select-all" | "expand-change" | "current-change" | "selection-change" | "cell-mouse-enter" | "cell-mouse-leave" | "cell-contextmenu" | "cell-click" | "cell-dblclick" | "row-click" | "row-contextmenu" | "row-dblclick" | "header-click" | "header-contextmenu" | "sort-change" | "filter-change" | "header-dragend", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    data: {
        type: import("vue").PropType<import("./table/defaults").DefaultRow[]>;
        default: () => never[];
    };
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    width: (NumberConstructor | StringConstructor)[];
    height: (NumberConstructor | StringConstructor)[];
    maxHeight: (NumberConstructor | StringConstructor)[];
    fit: {
        type: BooleanConstructor;
        default: boolean;
    };
    stripe: BooleanConstructor;
    border: BooleanConstructor;
    rowKey: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowKey"]>;
    showHeader: {
        type: BooleanConstructor;
        default: boolean;
    };
    showSummary: BooleanConstructor;
    sumText: StringConstructor;
    summaryMethod: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["summaryMethod"]>;
    rowClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowClassName"]>;
    rowStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["rowStyle"]>;
    cellClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["cellClassName"]>;
    cellStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["cellStyle"]>;
    headerRowClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerRowClassName"]>;
    headerRowStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerRowStyle"]>;
    headerCellClassName: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerCellClassName"]>;
    headerCellStyle: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["headerCellStyle"]>;
    highlightCurrentRow: BooleanConstructor;
    currentRowKey: (NumberConstructor | StringConstructor)[];
    emptyText: StringConstructor;
    expandRowKeys: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["expandRowKeys"]>;
    defaultExpandAll: BooleanConstructor;
    defaultSort: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["defaultSort"]>;
    tooltipEffect: StringConstructor;
    tooltipOptions: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["tooltipOptions"]>;
    spanMethod: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["spanMethod"]>;
    selectOnIndeterminate: {
        type: BooleanConstructor;
        default: boolean;
    };
    indent: {
        type: NumberConstructor;
        default: number;
    };
    treeProps: {
        type: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["treeProps"]>;
        default: () => {
            hasChildren: string;
            children: string;
            checkStrictly: boolean;
        };
    };
    lazy: BooleanConstructor;
    load: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["load"]>;
    style: {
        type: import("vue").PropType<import("vue").CSSProperties>;
        default: () => {};
    };
    className: {
        type: StringConstructor;
        default: string;
    };
    tableLayout: {
        type: import("vue").PropType<"fixed" | "auto">;
        default: string;
    };
    scrollbarAlwaysOn: BooleanConstructor;
    flexible: BooleanConstructor;
    showOverflowTooltip: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["showOverflowTooltip"]>;
    tooltipFormatter: import("vue").PropType<import("./table/defaults").TableProps<import("./table/defaults").DefaultRow>["tooltipFormatter"]>;
    appendFilterPanelTo: StringConstructor;
    scrollbarTabindex: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    allowDragLastColumn: {
        type: BooleanConstructor;
        default: boolean;
    };
    preserveExpandedContent: {
        type: BooleanConstructor;
        default: boolean;
    };
}>> & {
    onScroll?: ((...args: any[]) => any) | undefined;
    onSelect?: ((...args: any[]) => any) | undefined;
    "onExpand-change"?: ((...args: any[]) => any) | undefined;
    "onCurrent-change"?: ((...args: any[]) => any) | undefined;
    "onSelect-all"?: ((...args: any[]) => any) | undefined;
    "onSelection-change"?: ((...args: any[]) => any) | undefined;
    "onCell-mouse-enter"?: ((...args: any[]) => any) | undefined;
    "onCell-mouse-leave"?: ((...args: any[]) => any) | undefined;
    "onCell-contextmenu"?: ((...args: any[]) => any) | undefined;
    "onCell-click"?: ((...args: any[]) => any) | undefined;
    "onCell-dblclick"?: ((...args: any[]) => any) | undefined;
    "onRow-click"?: ((...args: any[]) => any) | undefined;
    "onRow-contextmenu"?: ((...args: any[]) => any) | undefined;
    "onRow-dblclick"?: ((...args: any[]) => any) | undefined;
    "onHeader-click"?: ((...args: any[]) => any) | undefined;
    "onHeader-contextmenu"?: ((...args: any[]) => any) | undefined;
    "onSort-change"?: ((...args: any[]) => any) | undefined;
    "onFilter-change"?: ((...args: any[]) => any) | undefined;
    "onHeader-dragend"?: ((...args: any[]) => any) | undefined;
}, {
    data: any[];
    style: import("vue").CSSProperties;
    tableLayout: "fixed" | "auto";
    border: boolean;
    className: string;
    fit: boolean;
    lazy: boolean;
    scrollbarAlwaysOn: boolean;
    allowDragLastColumn: boolean;
    stripe: boolean;
    treeProps: import("./table/defaults").TreeProps | undefined;
    showHeader: boolean;
    showSummary: boolean;
    highlightCurrentRow: boolean;
    defaultExpandAll: boolean;
    selectOnIndeterminate: boolean;
    indent: number;
    flexible: boolean;
    scrollbarTabindex: string | number;
    preserveExpandedContent: boolean;
}>;
export default _default;
