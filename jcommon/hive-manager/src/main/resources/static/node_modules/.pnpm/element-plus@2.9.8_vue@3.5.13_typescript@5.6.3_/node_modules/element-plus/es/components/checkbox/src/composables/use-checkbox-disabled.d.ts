import type { CheckboxModel, CheckboxStatus } from '../composables';
export declare const useCheckboxDisabled: ({ model, isChecked, }: Pick<CheckboxModel, "model"> & Pick<CheckboxStatus, "isChecked">) => {
    isDisabled: import("vue").ComputedRef<boolean>;
    isLimitDisabled: import("vue").ComputedRef<boolean>;
};
export type CheckboxDisabled = ReturnType<typeof useCheckboxDisabled>;
