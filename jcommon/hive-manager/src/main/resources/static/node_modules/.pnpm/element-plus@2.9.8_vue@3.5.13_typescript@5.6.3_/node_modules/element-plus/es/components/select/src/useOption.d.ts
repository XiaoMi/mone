import type { OptionProps, OptionStates } from './type';
export declare function useOption(props: OptionProps, states: OptionStates): {
    select: import("./type").SelectContext;
    currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
    currentValue: import("vue").ComputedRef<true | Record<string, any> | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
    itemSelected: import("vue").ComputedRef<boolean>;
    isDisabled: import("vue").ComputedRef<boolean>;
    hoverItem: () => void;
    updateOption: (query: string) => void;
};
