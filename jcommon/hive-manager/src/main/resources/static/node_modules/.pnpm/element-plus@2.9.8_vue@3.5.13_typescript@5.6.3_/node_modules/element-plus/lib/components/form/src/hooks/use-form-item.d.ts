import type { ComputedRef, Ref } from 'vue';
import type { FormItemContext } from '../types';
export declare const useFormItem: () => {
    form: import("../types").FormContext | undefined;
    formItem: FormItemContext | undefined;
};
export type IUseFormItemInputCommonProps = {
    id?: string;
    label?: string | number | boolean | Record<string, any>;
    ariaLabel?: string | number | boolean | Record<string, any>;
};
export declare const useFormItemInputId: (props: Partial<IUseFormItemInputCommonProps>, { formItemContext, disableIdGeneration, disableIdManagement, }: {
    formItemContext?: FormItemContext;
    disableIdGeneration?: ComputedRef<boolean> | Ref<boolean>;
    disableIdManagement?: ComputedRef<boolean> | Ref<boolean>;
}) => {
    isLabeledByFormItem: ComputedRef<boolean>;
    inputId: Ref<string | undefined>;
};
