declare function __VLS_template(): {
    empty?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeData) | (() => import("./types").TreeData) | ((new (...args: any[]) => import("./types").TreeData) | (() => import("./types").TreeData))[], unknown, unknown, () => [], boolean>;
    readonly emptyText: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly height: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 200, boolean>;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeOptionProps) | (() => import("./types").TreeOptionProps) | ((new (...args: any[]) => import("./types").TreeOptionProps) | (() => import("./types").TreeOptionProps))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly children: import("./virtual-tree").TreeOptionsEnum.CHILDREN;
        readonly label: import("./virtual-tree").TreeOptionsEnum.LABEL;
        readonly disabled: import("./virtual-tree").TreeOptionsEnum.DISABLED;
        readonly value: import("./virtual-tree").TreeOptionsEnum.KEY;
        readonly class: import("./virtual-tree").TreeOptionsEnum.CLASS;
    }>, boolean>;
    readonly highlightCurrent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly showCheckbox: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly defaultCheckedKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]) | ((new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]))[], unknown, unknown, () => [], boolean>;
    readonly checkStrictly: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly defaultExpandedKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]) | ((new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]))[], unknown, unknown, () => [], boolean>;
    readonly indent: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 16, boolean>;
    readonly itemSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly expandOnClickNode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly checkOnClickNode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly checkOnClickLeaf: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly currentNodeKey: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number) | (() => import("./types").TreeKey) | ((new (...args: any[]) => string | number) | (() => import("./types").TreeKey))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly accordion: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly filterMethod: {
        readonly type: import("vue").PropType<import("./types").FilterMethod>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly perfMode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}, {
    toggleCheckbox: (node: import("./types").TreeNode, isChecked: import("element-plus/es/components/checkbox").CheckboxValueType, nodeClick?: boolean, immediateUpdate?: boolean) => void;
    getCurrentNode: () => import("./types").TreeNodeData | undefined;
    getCurrentKey: () => import("./types").TreeKey | undefined;
    setCurrentKey: (key: import("./types").TreeKey) => void;
    getCheckedKeys: (leafOnly?: boolean) => import("./types").TreeKey[];
    getCheckedNodes: (leafOnly?: boolean) => import("./types").TreeNodeData[];
    getHalfCheckedKeys: () => import("./types").TreeKey[];
    getHalfCheckedNodes: () => import("./types").TreeNodeData[];
    setChecked: (key: import("./types").TreeKey, isChecked: boolean) => void;
    setCheckedKeys: (keys: import("./types").TreeKey[]) => void;
    filter: (query: string) => void;
    setData: (data: import("./types").TreeData) => void;
    getNode: (data: import("./types").TreeKey | import("./types").TreeNodeData) => import("./types").TreeNode | undefined;
    expandNode: (node: import("./types").TreeNode) => void;
    collapseNode: (node: import("./types").TreeNode) => void;
    setExpandedKeys: (keys: import("./types").TreeKey[]) => void;
    scrollToNode: (key: import("./types").TreeKey, strategy?: import("element-plus/es/components/virtual-list").Alignment) => void;
    scrollTo: (offset: number) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    check: (data: import("element-plus/es/components/tree").TreeNodeData, checkedInfo: import("./types").CheckedInfo) => void;
    "current-change": (data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => void;
    "node-expand": (data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => void;
    "check-change": (data: import("element-plus/es/components/tree").TreeNodeData, checked: boolean) => void;
    "node-click": (data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode, e: MouseEvent) => void;
    "node-contextmenu": (evt: Event, data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => void;
    "node-collapse": (data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => void;
    "node-drop": (data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode, e: DragEvent) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeData) | (() => import("./types").TreeData) | ((new (...args: any[]) => import("./types").TreeData) | (() => import("./types").TreeData))[], unknown, unknown, () => [], boolean>;
    readonly emptyText: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly height: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 200, boolean>;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeOptionProps) | (() => import("./types").TreeOptionProps) | ((new (...args: any[]) => import("./types").TreeOptionProps) | (() => import("./types").TreeOptionProps))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly children: import("./virtual-tree").TreeOptionsEnum.CHILDREN;
        readonly label: import("./virtual-tree").TreeOptionsEnum.LABEL;
        readonly disabled: import("./virtual-tree").TreeOptionsEnum.DISABLED;
        readonly value: import("./virtual-tree").TreeOptionsEnum.KEY;
        readonly class: import("./virtual-tree").TreeOptionsEnum.CLASS;
    }>, boolean>;
    readonly highlightCurrent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly showCheckbox: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly defaultCheckedKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]) | ((new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]))[], unknown, unknown, () => [], boolean>;
    readonly checkStrictly: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly defaultExpandedKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]) | ((new (...args: any[]) => import("./types").TreeKey[]) | (() => import("./types").TreeKey[]))[], unknown, unknown, () => [], boolean>;
    readonly indent: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 16, boolean>;
    readonly itemSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly expandOnClickNode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly checkOnClickNode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly checkOnClickLeaf: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly currentNodeKey: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | number) | (() => import("./types").TreeKey) | ((new (...args: any[]) => string | number) | (() => import("./types").TreeKey))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly accordion: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly filterMethod: {
        readonly type: import("vue").PropType<import("./types").FilterMethod>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly perfMode: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}>> & {
    "onCurrent-change"?: ((data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => any) | undefined;
    "onNode-expand"?: ((data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => any) | undefined;
    onCheck?: ((data: import("element-plus/es/components/tree").TreeNodeData, checkedInfo: import("./types").CheckedInfo) => any) | undefined;
    "onCheck-change"?: ((data: import("element-plus/es/components/tree").TreeNodeData, checked: boolean) => any) | undefined;
    "onNode-click"?: ((data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode, e: MouseEvent) => any) | undefined;
    "onNode-contextmenu"?: ((evt: Event, data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => any) | undefined;
    "onNode-collapse"?: ((data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode) => any) | undefined;
    "onNode-drop"?: ((data: import("element-plus/es/components/tree").TreeNodeData, node: import("./types").TreeNode, e: DragEvent) => any) | undefined;
}, {
    readonly data: import("./types").TreeData;
    readonly height: number;
    readonly props: import("./types").TreeOptionProps;
    readonly checkStrictly: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly accordion: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly perfMode: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly itemSize: number;
    readonly indent: number;
    readonly showCheckbox: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly defaultCheckedKeys: import("./types").TreeKey[];
    readonly defaultExpandedKeys: import("./types").TreeKey[];
    readonly expandOnClickNode: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly checkOnClickNode: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly checkOnClickLeaf: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly highlightCurrent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
