import type { TabsPaneContext } from './constants';
import type { ExtractPropTypes } from 'vue';
import type { Awaitable } from 'element-plus/es/utils';
export type TabPaneName = string | number;
export declare const tabsProps: {
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "card" | "border-card", unknown, "", boolean>;
    readonly closable: BooleanConstructor;
    readonly addable: BooleanConstructor;
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly editable: BooleanConstructor;
    readonly tabPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom" | "left" | "right", unknown, "top", boolean>;
    readonly beforeLeave: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => true, boolean>;
    readonly stretch: BooleanConstructor;
};
export type TabsProps = ExtractPropTypes<typeof tabsProps>;
export declare const tabsEmits: {
    "update:modelValue": (name: TabPaneName) => name is string | number;
    tabClick: (pane: TabsPaneContext, ev: Event) => boolean;
    tabChange: (name: TabPaneName) => name is string | number;
    edit: (paneName: TabPaneName | undefined, action: "remove" | "add") => boolean;
    tabRemove: (name: TabPaneName) => name is string | number;
    tabAdd: () => boolean;
};
export type TabsEmits = typeof tabsEmits;
export type TabsPanes = Record<number, TabsPaneContext>;
declare const Tabs: import("vue").DefineComponent<{
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "card" | "border-card", unknown, "", boolean>;
    readonly closable: BooleanConstructor;
    readonly addable: BooleanConstructor;
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly editable: BooleanConstructor;
    readonly tabPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom" | "left" | "right", unknown, "top", boolean>;
    readonly beforeLeave: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => true, boolean>;
    readonly stretch: BooleanConstructor;
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (name: TabPaneName) => name is string | number;
    tabClick: (pane: TabsPaneContext, ev: Event) => boolean;
    tabChange: (name: TabPaneName) => name is string | number;
    edit: (paneName: TabPaneName | undefined, action: "remove" | "add") => boolean;
    tabRemove: (name: TabPaneName) => name is string | number;
    tabAdd: () => boolean;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<ExtractPropTypes<{
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "card" | "border-card", unknown, "", boolean>;
    readonly closable: BooleanConstructor;
    readonly addable: BooleanConstructor;
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly editable: BooleanConstructor;
    readonly tabPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom" | "left" | "right", unknown, "top", boolean>;
    readonly beforeLeave: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | (() => (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>) | {
        (): (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => true, boolean>;
    readonly stretch: BooleanConstructor;
}>> & {
    "onUpdate:modelValue"?: ((name: TabPaneName) => any) | undefined;
    onTabClick?: ((pane: {
        uid: number;
        slots: import("vue").Slots;
        props: {
            readonly label: string;
            readonly disabled: boolean;
            readonly closable: boolean;
            readonly lazy: boolean;
            readonly name?: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown> | undefined;
        };
        paneName: string | number | undefined;
        active: boolean;
        index: string | undefined;
        isClosable: boolean;
    }, ev: Event) => any) | undefined;
    onTabRemove?: ((name: TabPaneName) => any) | undefined;
    onTabChange?: ((name: TabPaneName) => any) | undefined;
    onEdit?: ((paneName: TabPaneName | undefined, action: "add" | "remove") => any) | undefined;
    onTabAdd?: (() => any) | undefined;
}, {
    readonly stretch: boolean;
    readonly type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "card" | "border-card", unknown>;
    readonly closable: boolean;
    readonly beforeLeave: (newName: TabPaneName, oldName: TabPaneName) => Awaitable<void | boolean>;
    readonly editable: boolean;
    readonly tabPosition: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "top" | "bottom" | "left" | "right", unknown>;
    readonly addable: boolean;
}>;
export type TabsInstance = InstanceType<typeof Tabs> & {
    currentName: TabPaneName;
};
export default Tabs;
