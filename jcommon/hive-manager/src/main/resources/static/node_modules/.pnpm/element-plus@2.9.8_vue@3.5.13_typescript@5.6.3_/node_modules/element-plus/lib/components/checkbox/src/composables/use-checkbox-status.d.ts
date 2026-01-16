import type { ComponentInternalInstance } from 'vue';
import type { CheckboxProps } from '../checkbox';
import type { CheckboxModel } from '../composables';
export declare const useCheckboxStatus: (props: CheckboxProps, slots: ComponentInternalInstance["slots"], { model }: Pick<CheckboxModel, "model">) => {
    checkboxButtonSize: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    isChecked: import("vue").ComputedRef<boolean>;
    isFocused: import("vue").Ref<boolean>;
    checkboxSize: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    hasOwnLabel: import("vue").ComputedRef<boolean>;
    actualValue: import("vue").ComputedRef<string | number | boolean | Record<string, any> | undefined>;
};
export type CheckboxStatus = ReturnType<typeof useCheckboxStatus>;
