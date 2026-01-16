import type { Alignment as ScrollStrategy } from 'element-plus/es/components/virtual-list';
import type { SetupContext } from 'vue';
import type { treeEmits } from '../virtual-tree';
import type { CheckboxValueType } from 'element-plus/es/components/checkbox';
import type { Tree, TreeData, TreeKey, TreeNode, TreeNodeData, TreeProps } from '../types';
export declare function useTree(props: TreeProps, emit: SetupContext<typeof treeEmits>['emit']): {
    tree: import("vue").ShallowRef<Tree | undefined>;
    flattenTree: import("vue").ComputedRef<TreeNode[]>;
    isNotEmpty: import("vue").ComputedRef<boolean>;
    listRef: import("vue").Ref<import("vue").DefineComponent<{
        readonly className: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
        readonly containerElement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | Element) | (() => string | Element) | ((new (...args: any[]) => string | Element) | (() => string | Element))[], unknown, unknown, "div", boolean>;
        readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => any[]) | (() => any[]) | ((new (...args: any[]) => any[]) | (() => any[]))[], unknown, unknown, () => [], boolean>;
        readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "ltr" | "rtl", never, "ltr", false>;
        readonly height: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly innerElement: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, ObjectConstructor], unknown, unknown, "div", boolean>;
        readonly style: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly useIsScrolling: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
        readonly width: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [NumberConstructor, StringConstructor], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly perfMode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
        readonly scrollbarAlwaysOn: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
        readonly cache: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 2, false>;
        readonly estimatedItemSize: {
            readonly type: import("vue").PropType<number>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly layout: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", never, "vertical", false>;
        readonly initScrollOffset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 0, false>;
        readonly total: {
            readonly type: import("vue").PropType<number>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly itemSize: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | import("element-plus/es/components/virtual-list").ItemSize) | (() => number | import("element-plus/es/components/virtual-list").ItemSize) | ((new (...args: any[]) => number | import("element-plus/es/components/virtual-list").ItemSize) | (() => number | import("element-plus/es/components/virtual-list").ItemSize))[], never, never>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
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
        clientSize: import("vue").ComputedRef<string | number | undefined>;
        estimatedTotalSize: import("vue").ComputedRef<number>;
        windowStyle: import("vue").ComputedRef<(string | import("vue").CSSProperties | import("vue").StyleValue[] | {
            [x: string]: string;
            position: string;
            WebkitOverflowScrolling: string;
            willChange: string;
        } | undefined)[]>;
        windowRef: import("vue").Ref<HTMLElement | undefined>;
        innerRef: import("vue").Ref<HTMLElement | undefined>;
        innerStyle: import("vue").ComputedRef<{
            height: string;
            pointerEvents: string | undefined;
            width: string;
        }>;
        itemsToRender: import("vue").ComputedRef<number[]>;
        scrollbarRef: import("vue").Ref<any>;
        states: import("vue").Ref<{
            isScrolling: boolean;
            scrollDir: string;
            scrollOffset: number;
            updateRequested: boolean;
            isScrollbarDragging: boolean;
            scrollbarAlwaysOn: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
        }>;
        getItemStyle: (idx: number) => import("vue").CSSProperties;
        onScroll: (e: Event) => void;
        onScrollbarScroll: (distanceToGo: number, totalSteps: number) => void;
        onWheel: (e: WheelEvent) => void;
        scrollTo: (offset: number) => void;
        scrollToItem: (idx: number, alignment?: ScrollStrategy) => void;
        resetScrollTop: () => void;
    }, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("scroll" | "itemRendered")[], "scroll" | "itemRendered", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
        readonly className: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
        readonly containerElement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | Element) | (() => string | Element) | ((new (...args: any[]) => string | Element) | (() => string | Element))[], unknown, unknown, "div", boolean>;
        readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => any[]) | (() => any[]) | ((new (...args: any[]) => any[]) | (() => any[]))[], unknown, unknown, () => [], boolean>;
        readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "ltr" | "rtl", never, "ltr", false>;
        readonly height: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly innerElement: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, ObjectConstructor], unknown, unknown, "div", boolean>;
        readonly style: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | import("vue").StyleValue[]) | (() => import("vue").StyleValue))[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly useIsScrolling: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
        readonly width: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [NumberConstructor, StringConstructor], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly perfMode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
        readonly scrollbarAlwaysOn: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
        readonly cache: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 2, false>;
        readonly estimatedItemSize: {
            readonly type: import("vue").PropType<number>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly layout: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", never, "vertical", false>;
        readonly initScrollOffset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, never, never, 0, false>;
        readonly total: {
            readonly type: import("vue").PropType<number>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        readonly itemSize: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | import("element-plus/es/components/virtual-list").ItemSize) | (() => number | import("element-plus/es/components/virtual-list").ItemSize) | ((new (...args: any[]) => number | import("element-plus/es/components/virtual-list").ItemSize) | (() => number | import("element-plus/es/components/virtual-list").ItemSize))[], never, never>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
    }>> & {
        onScroll?: ((...args: any[]) => any) | undefined;
        onItemRendered?: ((...args: any[]) => any) | undefined;
    }, {
        readonly data: any[];
        readonly direction: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "ltr" | "rtl", never>;
        readonly layout: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "horizontal" | "vertical", never>;
        readonly className: string;
        readonly containerElement: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | Element) | (() => string | Element) | ((new (...args: any[]) => string | Element) | (() => string | Element))[], unknown, unknown>;
        readonly innerElement: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, ObjectConstructor], unknown, unknown>;
        readonly useIsScrolling: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
        readonly perfMode: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
        readonly scrollbarAlwaysOn: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
        readonly cache: number;
        readonly initScrollOffset: number;
    }> | undefined>;
    getKey: (node: TreeNodeData) => TreeKey;
    getChildren: (node: TreeNodeData) => TreeNodeData[];
    toggleExpand: (node: TreeNode) => void;
    toggleCheckbox: (node: TreeNode, isChecked: CheckboxValueType, nodeClick?: boolean, immediateUpdate?: boolean) => void;
    isExpanded: (node: TreeNode) => boolean;
    isChecked: (node: TreeNode) => boolean;
    isIndeterminate: (node: TreeNode) => boolean;
    isDisabled: (node: TreeNode) => boolean;
    isCurrent: (node: TreeNode) => boolean;
    isForceHiddenExpandIcon: (node: TreeNode) => boolean;
    handleNodeClick: (node: TreeNode, e: MouseEvent) => void;
    handleNodeDrop: (node: TreeNode, e: DragEvent) => void;
    handleNodeCheck: (node: TreeNode, checked: CheckboxValueType) => void;
    getCurrentNode: () => TreeNodeData | undefined;
    getCurrentKey: () => TreeKey | undefined;
    setCurrentKey: (key: TreeKey) => void;
    getCheckedKeys: (leafOnly?: boolean) => TreeKey[];
    getCheckedNodes: (leafOnly?: boolean) => TreeNodeData[];
    getHalfCheckedKeys: () => TreeKey[];
    getHalfCheckedNodes: () => TreeNodeData[];
    setChecked: (key: TreeKey, isChecked: boolean) => void;
    setCheckedKeys: (keys: TreeKey[]) => void;
    filter: (query: string) => void;
    setData: (data: TreeData) => void;
    getNode: (data: TreeKey | TreeNodeData) => TreeNode | undefined;
    expandNode: (node: TreeNode) => void;
    collapseNode: (node: TreeNode) => void;
    setExpandedKeys: (keys: TreeKey[]) => void;
    scrollToNode: (key: TreeKey, strategy?: ScrollStrategy) => void;
    scrollTo: (offset: number) => void;
};
