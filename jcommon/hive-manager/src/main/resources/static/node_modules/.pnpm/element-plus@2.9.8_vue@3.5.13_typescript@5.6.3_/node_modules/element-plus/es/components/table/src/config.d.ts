import type { VNode } from 'vue';
import type { TableColumnCtx } from './table-column/defaults';
import type { Store } from './store';
import type { TreeNode } from './table/defaults';
export declare const cellStarts: {
    default: {
        order: string;
    };
    selection: {
        width: number;
        minWidth: number;
        realWidth: number;
        order: string;
    };
    expand: {
        width: number;
        minWidth: number;
        realWidth: number;
        order: string;
    };
    index: {
        width: number;
        minWidth: number;
        realWidth: number;
        order: string;
    };
};
export declare const getDefaultClassName: (type: any) => any;
export declare const cellForced: {
    selection: {
        renderHeader<T>({ store, column }: {
            store: Store<T>;
        }): VNode<import("vue").RendererNode, import("vue").RendererElement, {
            [key: string]: any;
        }>;
        renderCell<T>({ row, column, store, $index, }: {
            row: T;
            column: TableColumnCtx<T>;
            store: Store<T>;
            $index: string;
        }): VNode<import("vue").RendererNode, import("vue").RendererElement, {
            [key: string]: any;
        }>;
        sortable: boolean;
        resizable: boolean;
    };
    index: {
        renderHeader<T>({ column }: {
            column: TableColumnCtx<T>;
        }): string;
        renderCell<T>({ column, $index, }: {
            column: TableColumnCtx<T>;
            $index: number;
        }): VNode<import("vue").RendererNode, import("vue").RendererElement, {
            [key: string]: any;
        }>;
        sortable: boolean;
    };
    expand: {
        renderHeader<T>({ column }: {
            column: TableColumnCtx<T>;
        }): string;
        renderCell<T>({ row, store, expanded, }: {
            row: T;
            store: Store<T>;
            expanded: boolean;
        }): VNode<import("vue").RendererNode, import("vue").RendererElement, {
            [key: string]: any;
        }>;
        sortable: boolean;
        resizable: boolean;
    };
};
export declare function defaultRenderCell<T>({ row, column, $index, }: {
    row: T;
    column: TableColumnCtx<T>;
    $index: number;
}): any;
export declare function treeCellPrefix<T>({ row, treeNode, store, }: {
    row: T;
    treeNode: TreeNode;
    store: Store<T>;
}, createPlaceholder?: boolean): VNode<import("vue").RendererNode, import("vue").RendererElement, {
    [key: string]: any;
}>[] | null;
