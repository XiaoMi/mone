import type { RadioButtonProps } from './radio-button';
import type { SetupContext } from 'vue';
import type { RadioEmits, RadioProps } from './radio';
export declare const useRadio: (props: RadioProps | RadioButtonProps, emit?: SetupContext<RadioEmits>["emit"]) => {
    radioRef: import("vue").Ref<HTMLInputElement | undefined>;
    isGroup: import("vue").ComputedRef<boolean>;
    radioGroup: import("./constants").RadioGroupContext | undefined;
    focus: import("vue").Ref<boolean>;
    size: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    disabled: import("vue").ComputedRef<boolean>;
    tabIndex: import("vue").ComputedRef<0 | -1>;
    modelValue: import("vue").WritableComputedRef<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown> | undefined>;
    actualValue: import("vue").ComputedRef<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown> | undefined>;
};
