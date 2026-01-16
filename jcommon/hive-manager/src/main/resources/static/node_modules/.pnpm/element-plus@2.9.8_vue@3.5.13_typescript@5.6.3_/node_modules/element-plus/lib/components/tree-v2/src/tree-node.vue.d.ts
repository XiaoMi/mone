import type { CheckboxValueType } from 'element-plus/es/components/checkbox';
import type { TreeNode } from './types';
declare const _default: import("vue").DefineComponent<{
    readonly node: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => TreeNode) | (() => TreeNode) | ((new (...args: any[]) => TreeNode) | (() => TreeNode))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly key: -1;
        readonly level: -1;
        readonly data: {};
    }>, boolean>;
    readonly expanded: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly checked: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly indeterminate: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly showCheckbox: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly disabled: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly current: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly hiddenExpandIcon: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly itemSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    click: (node: TreeNode, e: MouseEvent) => void;
    drop: (node: TreeNode, e: DragEvent) => void;
    toggle: (node: TreeNode) => void;
    check: (node: TreeNode, checked: CheckboxValueType) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly node: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => TreeNode) | (() => TreeNode) | ((new (...args: any[]) => TreeNode) | (() => TreeNode))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly key: -1;
        readonly level: -1;
        readonly data: {};
    }>, boolean>;
    readonly expanded: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly checked: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly indeterminate: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly showCheckbox: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly disabled: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly current: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly hiddenExpandIcon: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly itemSize: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
}>> & {
    onDrop?: ((node: TreeNode, e: DragEvent) => any) | undefined;
    onClick?: ((node: TreeNode, e: MouseEvent) => any) | undefined;
    onToggle?: ((node: TreeNode) => any) | undefined;
    onCheck?: ((node: TreeNode, checked: CheckboxValueType) => any) | undefined;
}, {
    readonly disabled: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly expanded: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly current: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly indeterminate: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly checked: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly node: TreeNode;
    readonly itemSize: number;
    readonly showCheckbox: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly hiddenExpandIcon: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
export default _default;
