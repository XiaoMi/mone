import type { ScrollStrategy } from './composables/use-scrollbar';
declare const TableV2: import("vue").DefineComponent<{
    readonly cache: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 2, false>;
    readonly estimatedRowHeight: {
        readonly default: undefined;
        readonly type: import("vue").PropType<number>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        readonly __epPropKey: true;
    };
    readonly rowKey: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType) | ((new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType))[], unknown, unknown, "id", boolean>;
    readonly headerClass: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("./table").HeaderClassNameGetter<any>) | (() => string | import("./table").HeaderClassNameGetter<any>) | ((new (...args: any[]) => string | import("./table").HeaderClassNameGetter<any>) | (() => string | import("./table").HeaderClassNameGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerCellProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerHeight: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | number[]) | (() => number | number[]) | ((new (...args: any[]) => number | number[]) | (() => number | number[]))[], unknown, unknown, 50, boolean>;
    readonly footerHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly rowClass: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("./table").RowClassNameGetter<any>) | (() => string | import("./table").RowClassNameGetter<any>) | ((new (...args: any[]) => string | import("./table").RowClassNameGetter<any>) | (() => string | import("./table").RowClassNameGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 50, boolean>;
    readonly cellProps: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | (() => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | ((new (...args: any[]) => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | (() => Record<string, any> | import("./table").ExtraCellPropGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly columns: {
        readonly type: import("vue").PropType<import("./common").AnyColumn[]>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly data: {
        readonly type: import("vue").PropType<any[]>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly dataGetter: {
        readonly type: import("vue").PropType<import("./types").DataGetter<any>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly fixedData: {
        readonly type: import("vue").PropType<any[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly expandColumnKey: StringConstructor;
    readonly expandedRowKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]) | ((new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]))[], unknown, unknown, () => never[], boolean>;
    readonly defaultExpandedRowKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]) | ((new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]))[], unknown, unknown, () => never[], boolean>;
    readonly class: StringConstructor;
    readonly fixed: BooleanConstructor;
    readonly style: {
        readonly type: import("vue").PropType<import("vue").CSSProperties>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly width: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly height: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly maxHeight: NumberConstructor;
    readonly useIsScrolling: BooleanConstructor;
    readonly indentSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 12, boolean>;
    readonly iconSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 12, boolean>;
    readonly hScrollbarSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly vScrollbarSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly scrollbarAlwaysOn: BooleanConstructor;
    readonly sortBy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").SortBy) | (() => import("element-plus").SortBy) | ((new (...args: any[]) => import("element-plus").SortBy) | (() => import("element-plus").SortBy))[], unknown, unknown, () => {
        key: import("./types").KeyType;
        order: import("element-plus").TableV2SortOrder;
    }, boolean>;
    readonly sortState: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").SortState) | (() => import("element-plus").SortState) | ((new (...args: any[]) => import("element-plus").SortState) | (() => import("element-plus").SortState))[], unknown, unknown, undefined, boolean>;
    readonly onColumnSort: {
        readonly type: import("vue").PropType<import("./table").ColumnSortHandler<any>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onExpandedRowsChange: {
        readonly type: import("vue").PropType<import("./table").ExpandedRowsChangeHandler>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onEndReached: {
        readonly type: import("vue").PropType<(distance: number) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onRowExpand: {
        readonly type: import("vue").PropType<import("element-plus").RowExpandHandler>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onScroll: {
        readonly type: import("vue").PropType<(...args: any[]) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onRowsRendered: {
        readonly type: import("vue").PropType<(params: import("./grid").onRowRenderedParams) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowEventHandlers: {
        readonly type: import("vue").PropType<import("element-plus").RowEventHandlers>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly cache: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 2, false>;
    readonly estimatedRowHeight: {
        readonly default: undefined;
        readonly type: import("vue").PropType<number>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        readonly __epPropKey: true;
    };
    readonly rowKey: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType) | ((new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType))[], unknown, unknown, "id", boolean>;
    readonly headerClass: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("./table").HeaderClassNameGetter<any>) | (() => string | import("./table").HeaderClassNameGetter<any>) | ((new (...args: any[]) => string | import("./table").HeaderClassNameGetter<any>) | (() => string | import("./table").HeaderClassNameGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerCellProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly headerHeight: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | number[]) | (() => number | number[]) | ((new (...args: any[]) => number | number[]) | (() => number | number[]))[], unknown, unknown, 50, boolean>;
    readonly footerHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly rowClass: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("./table").RowClassNameGetter<any>) | (() => string | import("./table").RowClassNameGetter<any>) | ((new (...args: any[]) => string | import("./table").RowClassNameGetter<any>) | (() => string | import("./table").RowClassNameGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowProps: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowHeight: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 50, boolean>;
    readonly cellProps: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | (() => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | ((new (...args: any[]) => Record<string, any> | import("./table").ExtraCellPropGetter<any>) | (() => Record<string, any> | import("./table").ExtraCellPropGetter<any>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly columns: {
        readonly type: import("vue").PropType<import("./common").AnyColumn[]>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly data: {
        readonly type: import("vue").PropType<any[]>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly dataGetter: {
        readonly type: import("vue").PropType<import("./types").DataGetter<any>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly fixedData: {
        readonly type: import("vue").PropType<any[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly expandColumnKey: StringConstructor;
    readonly expandedRowKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]) | ((new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]))[], unknown, unknown, () => never[], boolean>;
    readonly defaultExpandedRowKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]) | ((new (...args: any[]) => import("./types").KeyType[]) | (() => import("./types").KeyType[]))[], unknown, unknown, () => never[], boolean>;
    readonly class: StringConstructor;
    readonly fixed: BooleanConstructor;
    readonly style: {
        readonly type: import("vue").PropType<import("vue").CSSProperties>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly width: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly height: {
        readonly type: import("vue").PropType<number>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly maxHeight: NumberConstructor;
    readonly useIsScrolling: BooleanConstructor;
    readonly indentSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 12, boolean>;
    readonly iconSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 12, boolean>;
    readonly hScrollbarSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly vScrollbarSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly scrollbarAlwaysOn: BooleanConstructor;
    readonly sortBy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").SortBy) | (() => import("element-plus").SortBy) | ((new (...args: any[]) => import("element-plus").SortBy) | (() => import("element-plus").SortBy))[], unknown, unknown, () => {
        key: import("./types").KeyType;
        order: import("element-plus").TableV2SortOrder;
    }, boolean>;
    readonly sortState: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").SortState) | (() => import("element-plus").SortState) | ((new (...args: any[]) => import("element-plus").SortState) | (() => import("element-plus").SortState))[], unknown, unknown, undefined, boolean>;
    readonly onColumnSort: {
        readonly type: import("vue").PropType<import("./table").ColumnSortHandler<any>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onExpandedRowsChange: {
        readonly type: import("vue").PropType<import("./table").ExpandedRowsChangeHandler>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onEndReached: {
        readonly type: import("vue").PropType<(distance: number) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onRowExpand: {
        readonly type: import("vue").PropType<import("element-plus").RowExpandHandler>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onScroll: {
        readonly type: import("vue").PropType<(...args: any[]) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly onRowsRendered: {
        readonly type: import("vue").PropType<(params: import("./grid").onRowRenderedParams) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowEventHandlers: {
        readonly type: import("vue").PropType<import("element-plus").RowEventHandlers>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>>, {
    readonly fixed: boolean;
    readonly rowKey: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType) | ((new (...args: any[]) => string | number | symbol) | (() => import("./types").KeyType))[], unknown, unknown>;
    readonly useIsScrolling: boolean;
    readonly scrollbarAlwaysOn: boolean;
    readonly cache: number;
    readonly estimatedRowHeight: number;
    readonly rowHeight: number;
    readonly hScrollbarSize: number;
    readonly vScrollbarSize: number;
    readonly sortBy: import("element-plus").SortBy;
    readonly headerHeight: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | number[]) | (() => number | number[]) | ((new (...args: any[]) => number | number[]) | (() => number | number[]))[], unknown, unknown>;
    readonly footerHeight: number;
    readonly indentSize: number;
    readonly iconSize: number;
    readonly sortState: import("element-plus").SortState;
    readonly expandedRowKeys: import("./types").KeyType[];
    readonly defaultExpandedRowKeys: import("./types").KeyType[];
}>;
export default TableV2;
export type TableV2Instance = InstanceType<typeof TableV2> & {
    /**
     * @description scroll to a given position
     * @params params {{ scrollLeft?: number, scrollTop?: number }} where to scroll to.
     */
    scrollTo: (param: {
        scrollLeft?: number;
        scrollTop?: number;
    }) => void;
    /**
     * @description scroll to a given position horizontally
     * @params scrollLeft {Number} where to scroll to.
     */
    scrollToLeft: (scrollLeft: number) => void;
    /**
     * @description scroll to a given position vertically
     * @params scrollTop { Number } where to scroll to.
     */
    scrollToTop: (scrollTop: number) => void;
    /**
     * @description scroll to a given row
     * @params row {Number} which row to scroll to
     * @params strategy {ScrollStrategy} use what strategy to scroll to
     */
    scrollToRow(row: number, strategy?: ScrollStrategy): void;
};
