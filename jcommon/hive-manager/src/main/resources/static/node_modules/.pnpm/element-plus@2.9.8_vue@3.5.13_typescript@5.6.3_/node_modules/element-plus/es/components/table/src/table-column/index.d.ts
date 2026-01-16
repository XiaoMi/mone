import type { TableColumnCtx } from './defaults';
import type { DefaultRow } from '../table/defaults';
declare const _default: import("vue").DefineComponent<{
    type: {
        type: StringConstructor;
        default: string;
    };
    label: StringConstructor;
    className: StringConstructor;
    labelClassName: StringConstructor;
    property: StringConstructor;
    prop: StringConstructor;
    width: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    minWidth: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    renderHeader: import("vue").PropType<TableColumnCtx<DefaultRow>["renderHeader"]>;
    sortable: {
        type: (StringConstructor | BooleanConstructor)[];
        default: boolean;
    };
    sortMethod: import("vue").PropType<TableColumnCtx<DefaultRow>["sortMethod"]>;
    sortBy: import("vue").PropType<TableColumnCtx<DefaultRow>["sortBy"]>;
    resizable: {
        type: BooleanConstructor;
        default: boolean;
    };
    columnKey: StringConstructor;
    align: StringConstructor;
    headerAlign: StringConstructor;
    showOverflowTooltip: {
        type: import("vue").PropType<TableColumnCtx<DefaultRow>["showOverflowTooltip"]>;
        default: undefined;
    };
    tooltipFormatter: import("vue").PropType<TableColumnCtx<DefaultRow>["tooltipFormatter"]>;
    fixed: (StringConstructor | BooleanConstructor)[];
    formatter: import("vue").PropType<TableColumnCtx<DefaultRow>["formatter"]>;
    selectable: import("vue").PropType<TableColumnCtx<DefaultRow>["selectable"]>;
    reserveSelection: BooleanConstructor;
    filterMethod: import("vue").PropType<TableColumnCtx<DefaultRow>["filterMethod"]>;
    filteredValue: import("vue").PropType<TableColumnCtx<DefaultRow>["filteredValue"]>;
    filters: import("vue").PropType<TableColumnCtx<DefaultRow>["filters"]>;
    filterPlacement: StringConstructor;
    filterMultiple: {
        type: BooleanConstructor;
        default: boolean;
    };
    filterClassName: StringConstructor;
    index: import("vue").PropType<TableColumnCtx<DefaultRow>["index"]>;
    sortOrders: {
        type: import("vue").PropType<TableColumnCtx<DefaultRow>["sortOrders"]>;
        default: () => (string | null)[];
        validator: (val: TableColumnCtx<unknown>["sortOrders"]) => boolean;
    };
}, void, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    type: {
        type: StringConstructor;
        default: string;
    };
    label: StringConstructor;
    className: StringConstructor;
    labelClassName: StringConstructor;
    property: StringConstructor;
    prop: StringConstructor;
    width: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    minWidth: {
        type: (NumberConstructor | StringConstructor)[];
        default: string;
    };
    renderHeader: import("vue").PropType<TableColumnCtx<DefaultRow>["renderHeader"]>;
    sortable: {
        type: (StringConstructor | BooleanConstructor)[];
        default: boolean;
    };
    sortMethod: import("vue").PropType<TableColumnCtx<DefaultRow>["sortMethod"]>;
    sortBy: import("vue").PropType<TableColumnCtx<DefaultRow>["sortBy"]>;
    resizable: {
        type: BooleanConstructor;
        default: boolean;
    };
    columnKey: StringConstructor;
    align: StringConstructor;
    headerAlign: StringConstructor;
    showOverflowTooltip: {
        type: import("vue").PropType<TableColumnCtx<DefaultRow>["showOverflowTooltip"]>;
        default: undefined;
    };
    tooltipFormatter: import("vue").PropType<TableColumnCtx<DefaultRow>["tooltipFormatter"]>;
    fixed: (StringConstructor | BooleanConstructor)[];
    formatter: import("vue").PropType<TableColumnCtx<DefaultRow>["formatter"]>;
    selectable: import("vue").PropType<TableColumnCtx<DefaultRow>["selectable"]>;
    reserveSelection: BooleanConstructor;
    filterMethod: import("vue").PropType<TableColumnCtx<DefaultRow>["filterMethod"]>;
    filteredValue: import("vue").PropType<TableColumnCtx<DefaultRow>["filteredValue"]>;
    filters: import("vue").PropType<TableColumnCtx<DefaultRow>["filters"]>;
    filterPlacement: StringConstructor;
    filterMultiple: {
        type: BooleanConstructor;
        default: boolean;
    };
    filterClassName: StringConstructor;
    index: import("vue").PropType<TableColumnCtx<DefaultRow>["index"]>;
    sortOrders: {
        type: import("vue").PropType<TableColumnCtx<DefaultRow>["sortOrders"]>;
        default: () => (string | null)[];
        validator: (val: TableColumnCtx<unknown>["sortOrders"]) => boolean;
    };
}>>, {
    width: string | number;
    minWidth: string | number;
    type: string;
    showOverflowTooltip: boolean | Partial<Pick<import("element-plus/es/components/tooltip").ElTooltipProps, "offset" | "transition" | "placement" | "effect" | "showAfter" | "hideAfter" | "popperOptions" | "enterable" | "popperClass" | "appendTo" | "showArrow">> | undefined;
    sortOrders: ("ascending" | "descending" | null)[];
    sortable: string | boolean;
    resizable: boolean;
    reserveSelection: boolean;
    filterMultiple: boolean;
}>;
export default _default;
