import type { ExtractPropTypes, InjectionKey, Ref } from 'vue';
type EmptyValuesContext = ExtractPropTypes<typeof useEmptyValuesProps>;
export declare const emptyValuesContextKey: InjectionKey<Ref<EmptyValuesContext>>;
export declare const SCOPE = "use-empty-values";
export declare const DEFAULT_EMPTY_VALUES: (string | null | undefined)[];
export declare const DEFAULT_VALUE_ON_CLEAR: undefined;
export declare const useEmptyValuesProps: {
    readonly emptyValues: ArrayConstructor;
    readonly valueOnClear: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown, undefined, boolean>;
};
export declare const useEmptyValues: (props: EmptyValuesContext, defaultValue?: null | undefined) => {
    emptyValues: import("vue").ComputedRef<unknown[]>;
    valueOnClear: import("vue").ComputedRef<any>;
    isEmptyValue: (value: any) => boolean;
};
export {};
