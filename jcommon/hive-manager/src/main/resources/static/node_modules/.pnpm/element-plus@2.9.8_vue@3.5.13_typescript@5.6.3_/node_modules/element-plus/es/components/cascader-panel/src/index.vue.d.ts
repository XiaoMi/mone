import Node from './node';
import type { PropType } from 'vue';
import type { CascaderNodeValue, CascaderOption, CascaderValue, RenderLabel } from './node';
declare const _default: import("vue").DefineComponent<{
    border: {
        type: BooleanConstructor;
        default: boolean;
    };
    renderLabel: PropType<RenderLabel>;
    modelValue: {
        readonly type: PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number | import("./node").CascaderNodePathValue | (CascaderNodeValue | import("./node").CascaderNodePathValue)[]) | (() => CascaderValue) | ((new (...args: any[]) => string | number | import("./node").CascaderNodePathValue | (CascaderNodeValue | import("./node").CascaderNodePathValue)[]) | (() => CascaderValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => CascaderOption[]) | (() => CascaderOption[]) | ((new (...args: any[]) => CascaderOption[]) | (() => CascaderOption[]))[], unknown, unknown, () => CascaderOption[], boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./node").CascaderProps) | (() => import("./node").CascaderProps) | ((new (...args: any[]) => import("./node").CascaderProps) | (() => import("./node").CascaderProps))[], unknown, unknown, () => import("./node").CascaderProps, boolean>;
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
    menuList: import("vue").Ref<any[]>;
    menus: import("vue").Ref<{
        readonly uid: number;
        readonly level: number;
        readonly value: CascaderNodeValue;
        readonly label: string;
        readonly pathNodes: any[];
        readonly pathValues: CascaderNodeValue[];
        readonly pathLabels: string[];
        childrenData: {
            [x: string]: unknown;
            label?: string | undefined;
            value?: CascaderNodeValue | undefined;
            children?: any[] | undefined;
            disabled?: boolean | undefined;
            leaf?: boolean | undefined;
        }[] | undefined;
        children: any[];
        text: string;
        loaded: boolean;
        checked: boolean;
        indeterminate: boolean;
        loading: boolean;
        readonly data: {
            [x: string]: unknown;
            label?: string | undefined;
            value?: CascaderNodeValue | undefined;
            children?: any[] | undefined;
            disabled?: boolean | undefined;
            leaf?: boolean | undefined;
        } | null;
        readonly config: {
            expandTrigger: import("./node").ExpandTrigger;
            multiple: boolean;
            checkStrictly: boolean;
            emitPath: boolean;
            lazy: boolean;
            lazyLoad: import("./node").LazyLoad;
            value: string;
            label: string;
            children: string;
            disabled: string | import("./node").isDisabled;
            leaf: string | import("./node").isLeaf;
            hoverThreshold: number;
        };
        readonly parent?: any | undefined;
        readonly root: boolean;
        readonly isDisabled: boolean;
        readonly isLeaf: boolean;
        readonly valueByOption: CascaderNodeValue | CascaderNodeValue[];
        appendChild: (childData: CascaderOption) => Node;
        calcText: (allLevels: boolean, separator: string) => string;
        broadcast: (event: string, ...args: unknown[]) => void;
        emit: (event: string, ...args: unknown[]) => void;
        onParentCheck: (checked: boolean) => void;
        onChildCheck: () => void;
        setCheckState: (checked: boolean) => void;
        doCheck: (checked: boolean) => void;
    }[][]>;
    checkedNodes: import("vue").Ref<{
        readonly uid: number;
        readonly level: number;
        readonly value: CascaderNodeValue;
        readonly label: string;
        readonly pathNodes: any[];
        readonly pathValues: CascaderNodeValue[];
        readonly pathLabels: string[];
        childrenData: {
            [x: string]: unknown;
            label?: string | undefined;
            value?: CascaderNodeValue | undefined;
            children?: any[] | undefined;
            disabled?: boolean | undefined;
            leaf?: boolean | undefined;
        }[] | undefined;
        children: any[];
        text: string;
        loaded: boolean;
        checked: boolean;
        indeterminate: boolean;
        loading: boolean;
        readonly data: {
            [x: string]: unknown;
            label?: string | undefined;
            value?: CascaderNodeValue | undefined;
            children?: any[] | undefined;
            disabled?: boolean | undefined;
            leaf?: boolean | undefined;
        } | null;
        readonly config: {
            expandTrigger: import("./node").ExpandTrigger;
            multiple: boolean;
            checkStrictly: boolean;
            emitPath: boolean;
            lazy: boolean;
            lazyLoad: import("./node").LazyLoad;
            value: string;
            label: string;
            children: string;
            disabled: string | import("./node").isDisabled;
            leaf: string | import("./node").isLeaf;
            hoverThreshold: number;
        };
        readonly parent?: any | undefined;
        readonly root: boolean;
        readonly isDisabled: boolean;
        readonly isLeaf: boolean;
        readonly valueByOption: CascaderNodeValue | CascaderNodeValue[];
        appendChild: (childData: CascaderOption) => Node;
        calcText: (allLevels: boolean, separator: string) => string;
        broadcast: (event: string, ...args: unknown[]) => void;
        emit: (event: string, ...args: unknown[]) => void;
        onParentCheck: (checked: boolean) => void;
        onChildCheck: () => void;
        setCheckState: (checked: boolean) => void;
        doCheck: (checked: boolean) => void;
    }[]>;
    handleKeyDown: (e: KeyboardEvent) => void;
    handleCheckChange: (node: Node, checked: boolean, emitClose?: boolean) => void;
    getFlattedNodes: (leafOnly: boolean) => Node[] | undefined;
    /**
     * @description get an array of currently selected node,(leafOnly) whether only return the leaf checked nodes, default is `false`
     */
    getCheckedNodes: (leafOnly: boolean) => Node[] | undefined;
    /**
     * @description clear checked nodes
     */
    clearCheckedNodes: () => void;
    calculateCheckedValue: () => void;
    scrollToExpandingNode: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("update:modelValue" | "change" | "close" | "expand-change")[], "update:modelValue" | "change" | "close" | "expand-change", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    border: {
        type: BooleanConstructor;
        default: boolean;
    };
    renderLabel: PropType<RenderLabel>;
    modelValue: {
        readonly type: PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number | import("./node").CascaderNodePathValue | (CascaderNodeValue | import("./node").CascaderNodePathValue)[]) | (() => CascaderValue) | ((new (...args: any[]) => string | number | import("./node").CascaderNodePathValue | (CascaderNodeValue | import("./node").CascaderNodePathValue)[]) | (() => CascaderValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => CascaderOption[]) | (() => CascaderOption[]) | ((new (...args: any[]) => CascaderOption[]) | (() => CascaderOption[]))[], unknown, unknown, () => CascaderOption[], boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./node").CascaderProps) | (() => import("./node").CascaderProps) | ((new (...args: any[]) => import("./node").CascaderProps) | (() => import("./node").CascaderProps))[], unknown, unknown, () => import("./node").CascaderProps, boolean>;
}>> & {
    "onUpdate:modelValue"?: ((...args: any[]) => any) | undefined;
    onChange?: ((...args: any[]) => any) | undefined;
    onClose?: ((...args: any[]) => any) | undefined;
    "onExpand-change"?: ((...args: any[]) => any) | undefined;
}, {
    border: boolean;
    props: import("./node").CascaderProps;
    options: CascaderOption[];
}>;
export default _default;
