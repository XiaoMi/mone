import { ComputedRef } from 'vue';
import type { CSSProperties } from 'vue';
import type { TableV2Props } from '../table';
import type { UseColumnsReturn } from './use-columns';
type UseStyleProps = {
    columnsTotalWidth: UseColumnsReturn['columnsTotalWidth'];
    fixedColumnsOnLeft: UseColumnsReturn['fixedColumnsOnLeft'];
    fixedColumnsOnRight: UseColumnsReturn['fixedColumnsOnRight'];
    rowsHeight: ComputedRef<number>;
};
export declare const useStyles: (props: TableV2Props, { columnsTotalWidth, rowsHeight, fixedColumnsOnLeft, fixedColumnsOnRight, }: UseStyleProps) => {
    bodyWidth: ComputedRef<number>;
    fixedTableHeight: ComputedRef<number>;
    mainTableHeight: ComputedRef<number>;
    leftTableWidth: ComputedRef<number>;
    rightTableWidth: ComputedRef<number>;
    headerWidth: ComputedRef<number>;
    windowHeight: ComputedRef<number>;
    footerHeight: ComputedRef<CSSProperties>;
    emptyStyle: ComputedRef<CSSProperties>;
    rootStyle: ComputedRef<CSSProperties>;
    headerHeight: ComputedRef<number>;
};
export type UseStyleReturn = ReturnType<typeof useStyles>;
export {};
