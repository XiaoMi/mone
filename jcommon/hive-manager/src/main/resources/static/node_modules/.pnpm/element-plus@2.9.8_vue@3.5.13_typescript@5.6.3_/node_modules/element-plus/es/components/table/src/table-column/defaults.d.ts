import type { ComponentInternalInstance, PropType, Ref, VNode } from 'vue';
import type { DefaultRow, Table } from '../table/defaults';
import type { TableOverflowTooltipFormatter, TableOverflowTooltipOptions } from '../util';
type CI<T> = {
    column: TableColumnCtx<T>;
    $index: number;
};
type Filters = {
    text: string;
    value: string;
}[];
type FilterMethods<T> = (value: any, row: T, column: TableColumnCtx<T>) => void;
type ValueOf<T> = T[keyof T];
interface TableColumnCtx<T> {
    id: string;
    realWidth: number;
    type: string;
    label: string;
    className: string;
    labelClassName: string;
    property: string;
    prop: string;
    width: string | number;
    minWidth: string | number;
    renderHeader: (data: CI<T>) => VNode;
    sortable: boolean | string;
    sortMethod: (a: T, b: T) => number;
    sortBy: string | ((row: T, index: number) => string) | string[];
    resizable: boolean;
    columnKey: string;
    rawColumnKey: string;
    align: string;
    headerAlign: string;
    showOverflowTooltip?: boolean | TableOverflowTooltipOptions;
    tooltipFormatter?: TableOverflowTooltipFormatter<T>;
    fixed: boolean | string;
    formatter: (row: T, column: TableColumnCtx<T>, cellValue: any, index: number) => VNode | string;
    selectable: (row: T, index: number) => boolean;
    reserveSelection: boolean;
    filterMethod: FilterMethods<T>;
    filteredValue: string[];
    filters: Filters;
    filterPlacement: string;
    filterMultiple: boolean;
    filterClassName: string;
    index: number | ((index: number) => number);
    sortOrders: ('ascending' | 'descending' | null)[];
    renderCell: (data: any) => void;
    colSpan: number;
    rowSpan: number;
    children: TableColumnCtx<T>[];
    level: number;
    filterable: boolean | FilterMethods<T> | Filters;
    order: string;
    isColumnGroup: boolean;
    isSubColumn: boolean;
    columns: TableColumnCtx<T>[];
    getColumnIndex: () => number;
    no: number;
    filterOpened?: boolean;
}
interface TableColumn<T> extends ComponentInternalInstance {
    vnode: {
        vParent: TableColumn<T> | Table<T>;
    } & VNode;
    vParent: TableColumn<T> | Table<T>;
    columnId: string;
    columnConfig: Ref<Partial<TableColumnCtx<T>>>;
}
export type { Filters, FilterMethods, TableColumnCtx, TableColumn, ValueOf };
declare const _default: {
    /**
     * @description type of the column. If set to `selection`, the column will display checkbox. If set to `index`, the column will display index of the row (staring from 1). If set to `expand`, the column will display expand icon
     */
    type: {
        type: StringConstructor;
        default: string;
    };
    /**
     * @description column label
     */
    label: StringConstructor;
    /**
     * @description class name of cells in the column
     */
    className: StringConstructor;
    /**
     * @description class name of the label of this column
     */
    labelClassName: StringConstructor;
    /**
     * @description
     */
    property: StringConstructor;
    /**
     * @description field name. You can also use its alias: `property`
     */
    prop: StringConstructor;
    /**
     * @description column width
     */
    width: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    /**
     * @description column minimum width. Columns with `width` has a fixed width, while columns with `min-width` has a width that is distributed in proportion
     */
    minWidth: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    /**
     * @description render function for table header of this column
     */
    renderHeader: PropType<TableColumnCtx<DefaultRow>["renderHeader"]>;
    /**
     * @description whether column can be sorted. Remote sorting can be done by setting this attribute to 'custom' and listening to the `sort-change` event of Table
     */
    sortable: {
        type: (StringConstructor | BooleanConstructor)[];
        default: boolean;
    };
    /**
     * @description sorting method, works when `sortable` is `true`. Should return a number, just like Array.sort
     */
    sortMethod: PropType<TableColumnCtx<DefaultRow>["sortMethod"]>;
    /**
     * @description specify which property to sort by, works when `sortable` is `true` and `sort-method` is `undefined`. If set to an Array, the column will sequentially sort by the next property if the previous one is equal
     */
    sortBy: PropType<TableColumnCtx<DefaultRow>["sortBy"]>;
    /**
     * @description whether column width can be resized, works when `border` of `el-table` is `true`
     */
    resizable: {
        type: BooleanConstructor;
        default: boolean;
    };
    /**
     * @description column's key. If you need to use the filter-change event, you need this attribute to identify which column is being filtered
     */
    columnKey: StringConstructor;
    /**
     * @description alignment, the value should be 'left' \/ 'center' \/ 'right'
     */
    align: StringConstructor;
    /**
     * @description alignment of the table header. If omitted, the value of the above `align` attribute will be applied, the value should be 'left' \/ 'center' \/ 'right'
     */
    headerAlign: StringConstructor;
    /**
     * @description whether to hide extra content and show them in a tooltip when hovering on the cell
     */
    showOverflowTooltip: {
        type: PropType<TableColumnCtx<DefaultRow>["showOverflowTooltip"]>;
        default: undefined;
    };
    /**
     * @description function that formats cell tooltip content, works when `show-overflow-tooltip` is `true`
     */
    tooltipFormatter: PropType<TableColumnCtx<DefaultRow>["tooltipFormatter"]>;
    /**
     * @description whether column is fixed at left / right. Will be fixed at left if `true`
     */
    fixed: (StringConstructor | BooleanConstructor)[];
    /**
     * @description function that formats cell content
     */
    formatter: PropType<TableColumnCtx<DefaultRow>["formatter"]>;
    /**
     * @description function that determines if a certain row can be selected, works when `type` is 'selection'
     */
    selectable: PropType<TableColumnCtx<DefaultRow>["selectable"]>;
    /**
     * @description whether to reserve selection after data refreshing, works when `type` is 'selection'. Note that `row-key` is required for this to work
     */
    reserveSelection: BooleanConstructor;
    /**
     * @description data filtering method. If `filter-multiple` is on, this method will be called multiple times for each row, and a row will display if one of the calls returns `true`
     */
    filterMethod: PropType<TableColumnCtx<DefaultRow>["filterMethod"]>;
    /**
     * @description filter value for selected data, might be useful when table header is rendered with `render-header`
     */
    filteredValue: PropType<TableColumnCtx<DefaultRow>["filteredValue"]>;
    /**
     * @description an array of data filtering options. For each element in this array, `text` and `value` are required
     */
    filters: PropType<TableColumnCtx<DefaultRow>["filters"]>;
    /**
     * @description placement for the filter dropdown
     */
    filterPlacement: StringConstructor;
    /**
     * @description whether data filtering supports multiple options
     */
    filterMultiple: {
        type: BooleanConstructor;
        default: boolean;
    };
    /**
     * @description className for the filter dropdown
     */
    filterClassName: StringConstructor;
    /**
     * @description customize indices for each row, works on columns with `type=index`
     */
    index: PropType<TableColumnCtx<DefaultRow>["index"]>;
    /**
     * @description the order of the sorting strategies used when sorting the data, works when `sortable` is `true`. Accepts an array, as the user clicks on the header, the column is sorted in order of the elements in the array
     */
    sortOrders: {
        type: PropType<TableColumnCtx<DefaultRow>["sortOrders"]>;
        default: () => (string | null)[];
        validator: (val: TableColumnCtx<unknown>["sortOrders"]) => boolean;
    };
};
export default _default;
