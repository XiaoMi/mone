import Node from './model/node';
import type { ComponentInternalInstance, PropType } from 'vue';
import type { RootTreeType, TreeNodeData, TreeOptionProps } from './tree.type';
import type { CheckboxValueType } from 'element-plus/es/components/checkbox';
declare const _default: import("vue").DefineComponent<{
    node: {
        type: typeof Node;
        default: () => {};
    };
    props: {
        type: PropType<TreeOptionProps>;
        default: () => {};
    };
    accordion: BooleanConstructor;
    renderContent: FunctionConstructor;
    renderAfterExpand: BooleanConstructor;
    showCheckbox: {
        type: BooleanConstructor;
        default: boolean;
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
    node$: import("vue").Ref<HTMLElement | undefined>;
    tree: RootTreeType;
    expanded: import("vue").Ref<boolean>;
    childNodeRendered: import("vue").Ref<boolean>;
    oldChecked: import("vue").Ref<boolean | undefined>;
    oldIndeterminate: import("vue").Ref<boolean | undefined>;
    getNodeKey: (node: Node) => any;
    getNodeClass: (node: Node) => {
        [key: string]: boolean;
    };
    handleSelectChange: (checked: boolean, indeterminate: boolean) => void;
    handleClick: (e: MouseEvent) => void;
    handleContextMenu: (event: Event) => void;
    handleExpandIconClick: () => void;
    handleCheckChange: (value: CheckboxValueType) => void;
    handleChildNodeExpand: (nodeData: TreeNodeData, node: Node, instance: ComponentInternalInstance) => void;
    handleDragStart: (event: DragEvent) => void;
    handleDragOver: (event: DragEvent) => void;
    handleDrop: (event: DragEvent) => void;
    handleDragEnd: (event: DragEvent) => void;
    CaretRight: any;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, "node-expand"[], "node-expand", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    node: {
        type: typeof Node;
        default: () => {};
    };
    props: {
        type: PropType<TreeOptionProps>;
        default: () => {};
    };
    accordion: BooleanConstructor;
    renderContent: FunctionConstructor;
    renderAfterExpand: BooleanConstructor;
    showCheckbox: {
        type: BooleanConstructor;
        default: boolean;
    };
}>> & {
    "onNode-expand"?: ((...args: any[]) => any) | undefined;
}, {
    props: TreeOptionProps;
    node: Node;
    accordion: boolean;
    renderAfterExpand: boolean;
    showCheckbox: boolean;
}>;
export default _default;
