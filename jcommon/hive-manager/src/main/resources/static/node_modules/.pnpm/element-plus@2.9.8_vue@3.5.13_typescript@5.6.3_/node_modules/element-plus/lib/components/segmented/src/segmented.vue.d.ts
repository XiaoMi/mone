import type { Option } from './types';
declare function __VLS_template(): {
    default?(_: {
        item: Option;
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    ariaLabel: StringConstructor;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Option[]) | (() => Option[]) | ((new (...args: any[]) => Option[]) | (() => Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./segmented").Props) | (() => import("./segmented").Props) | ((new (...args: any[]) => import("./segmented").Props) | (() => import("./segmented").Props))[], unknown, unknown, () => Required<import("./segmented").Props>, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (val: any) => void;
    change: (val: any) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    ariaLabel: StringConstructor;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Option[]) | (() => Option[]) | ((new (...args: any[]) => Option[]) | (() => Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./segmented").Props) | (() => import("./segmented").Props) | ((new (...args: any[]) => import("./segmented").Props) | (() => import("./segmented").Props))[], unknown, unknown, () => Required<import("./segmented").Props>, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
}>> & {
    "onUpdate:modelValue"?: ((val: any) => any) | undefined;
    onChange?: ((val: any) => any) | undefined;
}, {
    disabled: boolean;
    direction: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown>;
    block: boolean;
    props: import("./segmented").Props;
    modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
    options: Option[];
    validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
