import type { ComponentInternalInstance } from 'vue';
import type { CheckboxProps } from '../checkbox';
export declare const useCheckbox: (props: CheckboxProps, slots: ComponentInternalInstance["slots"]) => {
    inputId: import("vue").Ref<string | undefined>;
    isLabeledByFormItem: import("vue").ComputedRef<boolean>;
    isChecked: import("vue").ComputedRef<boolean>;
    isDisabled: import("vue").ComputedRef<boolean>;
    isFocused: import("vue").Ref<boolean>;
    checkboxButtonSize: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    checkboxSize: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    hasOwnLabel: import("vue").ComputedRef<boolean>;
    model: import("vue").WritableComputedRef<any>;
    actualValue: import("vue").ComputedRef<string | number | boolean | Record<string, any> | undefined>;
    handleChange: (e: Event) => void;
    onClickRoot: (e: MouseEvent) => Promise<void>;
};
