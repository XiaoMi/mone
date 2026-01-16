import type { default as CascaderNode } from './node';
import type { PropType } from 'vue';
import type { CheckboxValueType } from 'element-plus/es/components/checkbox';
declare const _default: import("vue").DefineComponent<{
    node: {
        type: PropType<CascaderNode>;
        required: true;
    };
    menuId: StringConstructor;
}, {
    panel: import("./types").ElCascaderPanelContext;
    isHoverMenu: import("vue").ComputedRef<boolean>;
    multiple: import("vue").ComputedRef<boolean>;
    checkStrictly: import("vue").ComputedRef<boolean>;
    checkedNodeId: import("vue").ComputedRef<number>;
    isDisabled: import("vue").ComputedRef<boolean>;
    isLeaf: import("vue").ComputedRef<boolean>;
    expandable: import("vue").ComputedRef<boolean>;
    inExpandingPath: import("vue").ComputedRef<boolean>;
    inCheckedPath: import("vue").ComputedRef<boolean>;
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
    handleHoverExpand: (e: Event) => void;
    handleExpand: () => void;
    handleClick: () => void;
    handleCheck: (checked: boolean) => void;
    handleSelectCheck: (checked: CheckboxValueType | undefined) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, "expand"[], "expand", import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    node: {
        type: PropType<CascaderNode>;
        required: true;
    };
    menuId: StringConstructor;
}>> & {
    onExpand?: ((...args: any[]) => any) | undefined;
}, {}>;
export default _default;
