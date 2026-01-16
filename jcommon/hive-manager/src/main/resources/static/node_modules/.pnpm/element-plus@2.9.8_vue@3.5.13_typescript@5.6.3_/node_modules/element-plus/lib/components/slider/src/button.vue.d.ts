declare const _default: import("vue").DefineComponent<{
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly vertical: BooleanConstructor;
    readonly tooltipClass: StringConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, import("element-plus").Placement, unknown, "top", boolean>;
}, {
    onButtonDown: (event: MouseEvent | TouchEvent) => void;
    onKeyDown: (event: KeyboardEvent) => void;
    setPosition: (newPosition: number) => Promise<void>;
    hovering: import("vue").Ref<boolean>;
    dragging: import("vue").Ref<boolean>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: number) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly vertical: BooleanConstructor;
    readonly tooltipClass: StringConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, import("element-plus").Placement, unknown, "top", boolean>;
}>> & {
    "onUpdate:modelValue"?: ((value: number) => any) | undefined;
}, {
    readonly vertical: boolean;
    readonly modelValue: number;
    readonly placement: import("element-plus/es/utils").EpPropMergeType<StringConstructor, import("element-plus").Placement, unknown>;
}>;
export default _default;
