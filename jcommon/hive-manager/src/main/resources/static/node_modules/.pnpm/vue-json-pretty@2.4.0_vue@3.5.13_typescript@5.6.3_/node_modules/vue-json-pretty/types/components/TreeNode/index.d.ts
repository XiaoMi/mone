import { PropType, CSSProperties } from 'vue';
import { JSONFlattenReturnType } from '../../utils';
import './styles.less';
export interface NodeDataType extends JSONFlattenReturnType {
    id: number;
}
export declare const treeNodePropsPass: {
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
    theme: {
        type: PropType<"light" | "dark">;
        default: string;
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
};
declare const _default: import("vue").DefineComponent<{
    node: {
        type: PropType<NodeDataType>;
        required: true;
    };
    collapsed: BooleanConstructor;
    checked: BooleanConstructor;
    style: PropType<CSSProperties>;
    onSelectedChange: {
        type: PropType<(node: NodeDataType) => void>;
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
    theme: {
        type: PropType<"light" | "dark">;
        default: string;
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
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, ("nodeClick" | "bracketsClick" | "iconClick" | "selectedChange" | "valueChange")[], "nodeClick" | "bracketsClick" | "iconClick" | "selectedChange" | "valueChange", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    node: {
        type: PropType<NodeDataType>;
        required: true;
    };
    collapsed: BooleanConstructor;
    checked: BooleanConstructor;
    style: PropType<CSSProperties>;
    onSelectedChange: {
        type: PropType<(node: NodeDataType) => void>;
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
    theme: {
        type: PropType<"light" | "dark">;
        default: string;
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
    onValueChange?: ((...args: any[]) => any) | undefined;
}, {
    checked: boolean;
    collapsed: boolean;
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
}>;
export default _default;
