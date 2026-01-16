import type { Store } from '../store';
declare function useUtils<T>(store: Store<T>): {
    setCurrentRow: (row: T) => void;
    getSelectionRows: () => any;
    toggleRowSelection: (row: T, selected?: boolean, ignoreSelectable?: boolean) => void;
    clearSelection: () => void;
    clearFilter: (columnKeys?: string[]) => void;
    toggleAllSelection: () => void;
    toggleRowExpansion: (row: T, expanded?: boolean) => void;
    clearSort: () => void;
    sort: (prop: string, order: string) => void;
    updateKeyChildren: (key: string, data: T[]) => void;
};
export default useUtils;
