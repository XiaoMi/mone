import type { ExtractPropTypes } from 'vue';
import type Checkbox from './checkbox.vue';
export type CheckboxValueType = string | number | boolean;
export declare const checkboxProps: {
    ariaControls: StringConstructor;
    /**
     * @description binding value
     */
    modelValue: {
        type: (NumberConstructor | StringConstructor | BooleanConstructor)[];
        default: undefined;
    };
    /**
     * @description label of the Checkbox when used inside a `checkbox-group`
     */
    label: {
        type: (ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[];
        default: undefined;
    };
    /**
     * @description value of the Checkbox when used inside a `checkbox-group`
     */
    value: {
        type: (ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[];
        default: undefined;
    };
    /**
     * @description Set indeterminate state, only responsible for style control
     */
    indeterminate: BooleanConstructor;
    /**
     * @description whether the Checkbox is disabled
     */
    disabled: BooleanConstructor;
    /**
     * @description if the Checkbox is checked
     */
    checked: BooleanConstructor;
    /**
     * @description native 'name' attribute
     */
    name: {
        type: StringConstructor;
        default: undefined;
    };
    /**
     * @description value of the Checkbox if it's checked
     */
    trueValue: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    /**
     * @description value of the Checkbox if it's not checked
     */
    falseValue: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    /**
     * @deprecated use `trueValue` instead
     * @description value of the Checkbox if it's checked
     */
    trueLabel: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    /**
     * @deprecated use `falseValue` instead
     * @description value of the Checkbox if it's not checked
     */
    falseLabel: {
        type: (NumberConstructor | StringConstructor)[];
        default: undefined;
    };
    /**
     * @description input id
     */
    id: {
        type: StringConstructor;
        default: undefined;
    };
    /**
     * @description whether to add a border around Checkbox
     */
    border: BooleanConstructor;
    /**
     * @description size of the Checkbox
     */
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    /**
     * @description input tabindex
     */
    tabindex: (NumberConstructor | StringConstructor)[];
    /**
     * @description whether to trigger form validation
     */
    validateEvent: {
        type: BooleanConstructor;
        default: boolean;
    };
};
export declare const checkboxEmits: {
    "update:modelValue": (val: CheckboxValueType) => val is string | number | boolean;
    change: (val: CheckboxValueType) => val is string | number | boolean;
};
export type CheckboxProps = ExtractPropTypes<typeof checkboxProps>;
export type CheckboxEmits = typeof checkboxEmits;
export type CheckboxInstance = InstanceType<typeof Checkbox> & unknown;
