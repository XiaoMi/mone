import type { ExtractPropTypes } from 'vue';
export declare const carouselProps: {
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
};
export declare const carouselEmits: {
    /**
     * @description triggers when the active slide switches
     * @param current index of the new active slide
     * @param prev index of the old active slide
     */
    change: (current: number, prev: number) => boolean;
};
export type CarouselProps = ExtractPropTypes<typeof carouselProps>;
export type CarouselEmits = typeof carouselEmits;
