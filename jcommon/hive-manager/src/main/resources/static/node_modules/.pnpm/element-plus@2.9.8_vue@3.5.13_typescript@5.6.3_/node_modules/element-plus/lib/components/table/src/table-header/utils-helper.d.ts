import type { TableColumnCtx } from '../table-column/defaults';
import type { TableHeaderProps } from '.';
export declare const convertToRows: <T>(originColumns: TableColumnCtx<T>[]) => TableColumnCtx<T>[];
declare function useUtils<T>(props: TableHeaderProps<T>): {
    isGroup: import("vue").ComputedRef<boolean>;
    toggleAllSelection: (event: Event) => void;
    columnRows: import("vue").ComputedRef<TableColumnCtx<unknown>[]>;
};
export default useUtils;
