import type { TransferDataItem, TransferDirection } from './transfer';
import type { TransferPanelInstance } from './transfer-panel';
declare function __VLS_template(): {
    "left-empty"?(_: {}): any;
    "left-footer"?(_: {}): any;
    "right-empty"?(_: {}): any;
    "right-footer"?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => TransferDataItem[]) | (() => TransferDataItem[]) | ((new (...args: any[]) => TransferDataItem[]) | (() => TransferDataItem[]))[], unknown, unknown, () => never[], boolean>;
    readonly titles: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [string, string]) | (() => [string, string]) | ((new (...args: any[]) => [string, string]) | (() => [string, string]))[], unknown, unknown, () => never[], boolean>;
    readonly buttonTexts: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [string, string]) | (() => [string, string]) | ((new (...args: any[]) => [string, string]) | (() => [string, string]))[], unknown, unknown, () => never[], boolean>;
    readonly filterPlaceholder: StringConstructor;
    readonly filterMethod: {
        readonly type: import("vue").PropType<(query: string, item: TransferDataItem) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly leftDefaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly rightDefaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly renderContent: {
        readonly type: import("vue").PropType<import("./transfer").renderContent>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferFormat) | (() => import("./transfer").TransferFormat) | ((new (...args: any[]) => import("./transfer").TransferFormat) | (() => import("./transfer").TransferFormat))[], unknown, unknown, () => {}, boolean>;
    readonly filterable: BooleanConstructor;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferPropsAlias) | (() => import("./transfer").TransferPropsAlias) | ((new (...args: any[]) => import("./transfer").TransferPropsAlias) | (() => import("./transfer").TransferPropsAlias))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly label: "label";
        readonly key: "key";
        readonly disabled: "disabled";
    }>, boolean>;
    readonly targetOrder: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "push" | "unshift" | "original", unknown, "original", boolean>;
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}, {
    /** @description clear the filter keyword of a certain panel */
    clearQuery: (which: TransferDirection) => void;
    /** @description left panel ref */
    leftPanel: import("vue").Ref<TransferPanelInstance | undefined>;
    /** @description right panel ref */
    rightPanel: import("vue").Ref<TransferPanelInstance | undefined>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: import("./transfer").TransferKey[]) => void;
    change: (value: import("./transfer").TransferKey[], direction: TransferDirection, movedKeys: import("./transfer").TransferKey[]) => void;
    "left-check-change": (value: import("./transfer").TransferKey[], movedKeys?: import("./transfer").TransferKey[] | undefined) => void;
    "right-check-change": (value: import("./transfer").TransferKey[], movedKeys?: import("./transfer").TransferKey[] | undefined) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => TransferDataItem[]) | (() => TransferDataItem[]) | ((new (...args: any[]) => TransferDataItem[]) | (() => TransferDataItem[]))[], unknown, unknown, () => never[], boolean>;
    readonly titles: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [string, string]) | (() => [string, string]) | ((new (...args: any[]) => [string, string]) | (() => [string, string]))[], unknown, unknown, () => never[], boolean>;
    readonly buttonTexts: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => [string, string]) | (() => [string, string]) | ((new (...args: any[]) => [string, string]) | (() => [string, string]))[], unknown, unknown, () => never[], boolean>;
    readonly filterPlaceholder: StringConstructor;
    readonly filterMethod: {
        readonly type: import("vue").PropType<(query: string, item: TransferDataItem) => boolean>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly leftDefaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly rightDefaultChecked: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly renderContent: {
        readonly type: import("vue").PropType<import("./transfer").renderContent>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]) | ((new (...args: any[]) => import("./transfer").TransferKey[]) | (() => import("./transfer").TransferKey[]))[], unknown, unknown, () => never[], boolean>;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferFormat) | (() => import("./transfer").TransferFormat) | ((new (...args: any[]) => import("./transfer").TransferFormat) | (() => import("./transfer").TransferFormat))[], unknown, unknown, () => {}, boolean>;
    readonly filterable: BooleanConstructor;
    readonly props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./transfer").TransferPropsAlias) | (() => import("./transfer").TransferPropsAlias) | ((new (...args: any[]) => import("./transfer").TransferPropsAlias) | (() => import("./transfer").TransferPropsAlias))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{
        readonly label: "label";
        readonly key: "key";
        readonly disabled: "disabled";
    }>, boolean>;
    readonly targetOrder: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "push" | "unshift" | "original", unknown, "original", boolean>;
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}>> & {
    "onUpdate:modelValue"?: ((value: import("./transfer").TransferKey[]) => any) | undefined;
    onChange?: ((value: import("./transfer").TransferKey[], direction: TransferDirection, movedKeys: import("./transfer").TransferKey[]) => any) | undefined;
    "onLeft-check-change"?: ((value: import("./transfer").TransferKey[], movedKeys?: import("./transfer").TransferKey[] | undefined) => any) | undefined;
    "onRight-check-change"?: ((value: import("./transfer").TransferKey[], movedKeys?: import("./transfer").TransferKey[] | undefined) => any) | undefined;
}, {
    readonly data: TransferDataItem[];
    readonly props: import("./transfer").TransferPropsAlias;
    readonly titles: [string, string];
    readonly modelValue: import("./transfer").TransferKey[];
    readonly validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly format: import("./transfer").TransferFormat;
    readonly filterable: boolean;
    readonly buttonTexts: [string, string];
    readonly leftDefaultChecked: import("./transfer").TransferKey[];
    readonly rightDefaultChecked: import("./transfer").TransferKey[];
    readonly targetOrder: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "push" | "unshift" | "original", unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
