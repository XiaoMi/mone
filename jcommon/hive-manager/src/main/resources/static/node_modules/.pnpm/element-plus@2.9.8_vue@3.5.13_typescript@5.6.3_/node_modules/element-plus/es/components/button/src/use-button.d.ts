import type { SetupContext } from 'vue';
import type { ButtonEmits, ButtonProps } from './button';
export declare const useButton: (props: ButtonProps, emit: SetupContext<ButtonEmits>["emit"]) => {
    _disabled: import("vue").ComputedRef<boolean>;
    _size: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    _type: import("vue").ComputedRef<"text" | "" | "default" | "success" | "warning" | "info" | "primary" | "danger">;
    _ref: import("vue").Ref<HTMLButtonElement | undefined>;
    _props: import("vue").ComputedRef<{
        ariaDisabled: boolean;
        disabled: boolean;
        autofocus: boolean;
        type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "button" | "reset" | "submit", unknown>;
    } | {
        ariaDisabled?: undefined;
        disabled?: undefined;
        autofocus?: undefined;
        type?: undefined;
    }>;
    shouldAddSpace: import("vue").ComputedRef<boolean>;
    handleClick: (evt: MouseEvent) => void;
};
