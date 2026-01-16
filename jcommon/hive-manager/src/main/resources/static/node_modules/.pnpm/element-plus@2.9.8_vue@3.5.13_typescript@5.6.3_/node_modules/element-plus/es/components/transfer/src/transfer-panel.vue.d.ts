import type { VNode } from 'vue';
declare function __VLS_template(): {
    empty?(_: {}): any;
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferDataItem[]) | (() => import("element-plus").TransferDataItem[]) | ((new (...args: any[]) => import("element-plus").TransferDataItem[]) | (() => import("element-plus").TransferDataItem[]))[], unknown, unknown, () => never[], boolean>;
    readonly optionRender: {
        readonly type: import("vue").PropType<(option: import("element-plus").TransferDataItem) => VNode | VNode[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly placeholder: StringConstructor;
    readonly title: StringConstructor;
    readonly filterable: BooleanConstructor;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferFormat) | (() => import("element-plus").TransferFormat) | ((new (...args: any[]) => import("element-plus").TransferFormat) | (() => import("element-plus").TransferFormat))[], unknown, unknown, () => {}, boolean>;
    readonly filterMethod: {
        readonly type: import("vue").PropType<(query: string, item: import("element-plus").TransferDataItem) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly defaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferKey[]) | (() => import("element-plus").TransferKey[]) | ((new (...args: any[]) => import("element-plus").TransferKey[]) | (() => import("element-plus").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferPropsAlias) | (() => import("element-plus").TransferPropsAlias) | ((new (...args: any[]) => import("element-plus").TransferPropsAlias) | (() => import("element-plus").TransferPropsAlias))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly label: "label";
        readonly key: "key";
        readonly disabled: "disabled";
    }>, boolean>;
}, {
    /** @description filter keyword */
    query: import("vue").Ref<string>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "checked-change": (value: import("element-plus").TransferKey[], movedKeys?: import("element-plus").TransferKey[] | undefined) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferDataItem[]) | (() => import("element-plus").TransferDataItem[]) | ((new (...args: any[]) => import("element-plus").TransferDataItem[]) | (() => import("element-plus").TransferDataItem[]))[], unknown, unknown, () => never[], boolean>;
    readonly optionRender: {
        readonly type: import("vue").PropType<(option: import("element-plus").TransferDataItem) => VNode | VNode[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly placeholder: StringConstructor;
    readonly title: StringConstructor;
    readonly filterable: BooleanConstructor;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferFormat) | (() => import("element-plus").TransferFormat) | ((new (...args: any[]) => import("element-plus").TransferFormat) | (() => import("element-plus").TransferFormat))[], unknown, unknown, () => {}, boolean>;
    readonly filterMethod: {
        readonly type: import("vue").PropType<(query: string, item: import("element-plus").TransferDataItem) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly defaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferKey[]) | (() => import("element-plus").TransferKey[]) | ((new (...args: any[]) => import("element-plus").TransferKey[]) | (() => import("element-plus").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus").TransferPropsAlias) | (() => import("element-plus").TransferPropsAlias) | ((new (...args: any[]) => import("element-plus").TransferPropsAlias) | (() => import("element-plus").TransferPropsAlias))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly label: "label";
        readonly key: "key";
        readonly disabled: "disabled";
    }>, boolean>;
}>> & {
    "onChecked-change"?: ((value: import("element-plus").TransferKey[], movedKeys?: import("element-plus").TransferKey[] | undefined) => any) | undefined;
}, {
    readonly data: import("element-plus").TransferDataItem[];
    readonly props: import("element-plus").TransferPropsAlias;
    readonly format: import("element-plus").TransferFormat;
    readonly filterable: boolean;
    readonly defaultChecked: import("element-plus").TransferKey[];
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
