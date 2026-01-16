import { PropType, CSSProperties } from 'vue';
import { NodeDataType } from '../../components/TreeNode';
import { JSONDataType } from '../../utils';
import './styles.less';
declare const _default: import("vue").DefineComponent<{
    data: {
        type: PropType<JSONDataType>;
        default: null;
    };
    collapsedNodeLength: {
        type: NumberConstructor;
        default: number;
    };
    deep: {
        type: NumberConstructor;
        default: number;
    };
    pathCollapsible: {
        type: PropType<(node: NodeDataType) => boolean>;
        default: () => boolean;
    };
    rootPath: {
        type: StringConstructor;
        default: string;
    };
    virtual: {
        type: BooleanConstructor;
        default: boolean;
    };
    height: {
        type: NumberConstructor;
        default: number;
    };
    itemHeight: {
        type: NumberConstructor;
        default: number;
    };
    selectedValue: {
        type: PropType<string | string[]>;
        default: () => string;
    };
    collapsedOnClickBrackets: {
        type: BooleanConstructor;
        default: boolean;
    };
    style: PropType<CSSProperties>;
    onSelectedChange: {
        type: PropType<(newVal: string | string[], oldVal: string | string[]) => void>;
    };
    theme: {
        type: PropType<"light" | "dark">;
        default: string;
    };
    showLength: {
        type: BooleanConstructor;
        default: boolean;
    };
    showDoubleQuotes: {
        type: BooleanConstructor;
        default: boolean;
    };
    renderNodeKey: PropType<(opt: {
        node: NodeDataType;
        defaultKey: string | JSX.Element;
    }) => unknown>;
    renderNodeValue: PropType<(opt: {
        node: NodeDataType;
        defaultValue: string | JSX.Element;
    }) => unknown>;
    selectableType: PropType<"" | "multiple" | "single">;
    showSelectController: {
        type: BooleanConstructor;
        default: boolean;
    };
    showLine: {
        type: BooleanConstructor;
        default: boolean;
    };
    showLineNumber: {
        type: BooleanConstructor;
        default: boolean;
    };
    selectOnClickNode: {
        type: BooleanConstructor;
        default: boolean;
    };
    nodeSelectable: {
        type: PropType<(node: NodeDataType) => boolean>;
        default: () => boolean;
    };
    highlightSelectedNode: {
        type: BooleanConstructor;
        default: boolean;
    };
    showIcon: {
        type: BooleanConstructor;
        default: boolean;
    };
    showKeyValueSpace: {
        type: BooleanConstructor;
        default: boolean;
    };
    editable: {
        type: BooleanConstructor;
        default: boolean;
    };
    editableTrigger: {
        type: PropType<"click" | "dblclick">;
        default: string;
    };
    onNodeClick: {
        type: PropType<(node: NodeDataType) => void>;
    };
    onBracketsClick: {
        type: PropType<(collapsed: boolean, node: NodeDataType) => void>;
    };
    onIconClick: {
        type: PropType<(collapsed: boolean, node: NodeDataType) => void>;
    };
    onValueChange: {
        type: PropType<(value: boolean, path: string) => void>;
    };
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("nodeClick" | "bracketsClick" | "iconClick" | "selectedChange" | "update:selectedValue" | "update:data")[], "nodeClick" | "bracketsClick" | "iconClick" | "selectedChange" | "update:selectedValue" | "update:data", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    data: {
        type: PropType<JSONDataType>;
        default: null;
    };
    collapsedNodeLength: {
        type: NumberConstructor;
        default: number;
    };
    deep: {
        type: NumberConstructor;
        default: number;
    };
    pathCollapsible: {
        type: PropType<(node: NodeDataType) => boolean>;
        default: () => boolean;
    };
    rootPath: {
        type: StringConstructor;
        default: string;
    };
    virtual: {
        type: BooleanConstructor;
        default: boolean;
    };
    height: {
        type: NumberConstructor;
        default: number;
    };
    itemHeight: {
        type: NumberConstructor;
        default: number;
    };
    selectedValue: {
        type: PropType<string | string[]>;
        default: () => string;
    };
    collapsedOnClickBrackets: {
        type: BooleanConstructor;
        default: boolean;
    };
    style: PropType<CSSProperties>;
    onSelectedChange: {
        type: PropType<(newVal: string | string[], oldVal: string | string[]) => void>;
    };
    theme: {
        type: PropType<"light" | "dark">;
        default: string;
    };
    showLength: {
        type: BooleanConstructor;
        default: boolean;
    };
    showDoubleQuotes: {
        type: BooleanConstructor;
        default: boolean;
    };
    renderNodeKey: PropType<(opt: {
        node: NodeDataType;
        defaultKey: string | JSX.Element;
    }) => unknown>;
    renderNodeValue: PropType<(opt: {
        node: NodeDataType;
        defaultValue: string | JSX.Element;
    }) => unknown>;
    selectableType: PropType<"" | "multiple" | "single">;
    showSelectController: {
        type: BooleanConstructor;
        default: boolean;
    };
    showLine: {
        type: BooleanConstructor;
        default: boolean;
    };
    showLineNumber: {
        type: BooleanConstructor;
        default: boolean;
    };
    selectOnClickNode: {
        type: BooleanConstructor;
        default: boolean;
    };
    nodeSelectable: {
        type: PropType<(node: NodeDataType) => boolean>;
        default: () => boolean;
    };
    highlightSelectedNode: {
        type: BooleanConstructor;
        default: boolean;
    };
    showIcon: {
        type: BooleanConstructor;
        default: boolean;
    };
    showKeyValueSpace: {
        type: BooleanConstructor;
        default: boolean;
    };
    editable: {
        type: BooleanConstructor;
        default: boolean;
    };
    editableTrigger: {
        type: PropType<"click" | "dblclick">;
        default: string;
    };
    onNodeClick: {
        type: PropType<(node: NodeDataType) => void>;
    };
    onBracketsClick: {
        type: PropType<(collapsed: boolean, node: NodeDataType) => void>;
    };
    onIconClick: {
        type: PropType<(collapsed: boolean, node: NodeDataType) => void>;
    };
    onValueChange: {
        type: PropType<(value: boolean, path: string) => void>;
    };
}>> & {
    onNodeClick?: ((...args: any[]) => any) | undefined;
    onBracketsClick?: ((...args: any[]) => any) | undefined;
    onIconClick?: ((...args: any[]) => any) | undefined;
    onSelectedChange?: ((...args: any[]) => any) | undefined;
    "onUpdate:selectedValue"?: ((...args: any[]) => any) | undefined;
    "onUpdate:data"?: ((...args: any[]) => any) | undefined;
}, {
    data: JSONDataType;
    showLength: boolean;
    showDoubleQuotes: boolean;
    showSelectController: boolean;
    showLine: boolean;
    showLineNumber: boolean;
    selectOnClickNode: boolean;
    nodeSelectable: (node: NodeDataType) => boolean;
    highlightSelectedNode: boolean;
    showIcon: boolean;
    theme: "light" | "dark";
    showKeyValueSpace: boolean;
    editable: boolean;
    editableTrigger: "click" | "dblclick";
    collapsedNodeLength: number;
    deep: number;
    pathCollapsible: (node: NodeDataType) => boolean;
    rootPath: string;
    virtual: boolean;
    height: number;
    itemHeight: number;
    selectedValue: string | string[];
    collapsedOnClickBrackets: boolean;
}>;
export default _default;
