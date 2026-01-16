declare function __VLS_template(): {
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly initialIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly height: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly trigger: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "click" | "hover", unknown, "hover", boolean>;
    readonly autoplay: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly interval: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3000, boolean>;
    readonly indicatorPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "none" | "outside", unknown, "", boolean>;
    readonly arrow: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "always" | "never" | "hover", unknown, "hover", boolean>;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "card", unknown, "", boolean>;
    readonly cardScale: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0.83, boolean>;
    readonly loop: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly pauseOnHover: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly motionBlur: BooleanConstructor;
}, {
    /** @description active slide index */
    activeIndex: import("vue").Ref<number>;
    /** @description manually switch slide, index of the slide to be switched to, starting from 0; or the `name` of corresponding `el-carousel-item` */
    setActiveItem: (index: number | string) => void;
    /** @description switch to the previous slide */
    prev: () => void;
    /** @description switch to the next slide */
    next: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    change: (current: number, prev: number) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly initialIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly height: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly trigger: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "click" | "hover", unknown, "hover", boolean>;
    readonly autoplay: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly interval: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3000, boolean>;
    readonly indicatorPosition: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "none" | "outside", unknown, "", boolean>;
    readonly arrow: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "always" | "never" | "hover", unknown, "hover", boolean>;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "card", unknown, "", boolean>;
    readonly cardScale: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0.83, boolean>;
    readonly loop: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly pauseOnHover: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly motionBlur: BooleanConstructor;
}>> & {
    onChange?: ((current: number, prev: number) => any) | undefined;
}, {
    readonly height: string;
    readonly direction: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "horizontal" | "vertical", unknown>;
    readonly type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "card", unknown>;
    readonly arrow: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "always" | "never" | "hover", unknown>;
    readonly trigger: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "click" | "hover", unknown>;
    readonly loop: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly interval: number;
    readonly initialIndex: number;
    readonly autoplay: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly indicatorPosition: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "none" | "outside", unknown>;
    readonly cardScale: number;
    readonly pauseOnHover: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly motionBlur: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
