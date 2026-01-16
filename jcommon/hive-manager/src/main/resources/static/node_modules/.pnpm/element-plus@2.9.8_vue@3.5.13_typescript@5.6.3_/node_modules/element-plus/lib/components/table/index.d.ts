import Table from './src/table.vue';
import TableColumn from './src/tableColumn';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElTable: SFCWithInstall<typeof Table> & {
    TableColumn: typeof TableColumn;
};
export default ElTable;
export declare const ElTableColumn: SFCWithInstall<typeof TableColumn>;
export type TableInstance = InstanceType<typeof Table> & unknown;
export type TableColumnInstance = InstanceType<typeof TableColumn> & unknown;
export type { SummaryMethod, Table, TableProps, TableRefs, ColumnCls, ColumnStyle, CellCls, CellStyle, TreeNode, RenderRowData, Sort, Filter, TableColumnCtx, TableTooltipData, } from './src/table/defaults';
