declare const _default: import("vue").DefineComponent<{
    readonly color: {
        readonly type: import("vue").PropType<import("../utils/color").default>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly vertical: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
}, {
    /**
     * @description update alpha slider manually
     * @type {Function}
     */
    update: () => void;
    /**
     * @description bar element ref
     * @type {HTMLElement}
     */
    bar: import("vue").ShallowRef<HTMLElement | undefined>;
    /**
     * @description thumb element ref
     * @type {HTMLElement}
     */
    thumb: import("vue").ShallowRef<HTMLElement | undefined>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly color: {
        readonly type: import("vue").PropType<import("../utils/color").default>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly vertical: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
}>>, {
    readonly vertical: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}>;
export default _default;
