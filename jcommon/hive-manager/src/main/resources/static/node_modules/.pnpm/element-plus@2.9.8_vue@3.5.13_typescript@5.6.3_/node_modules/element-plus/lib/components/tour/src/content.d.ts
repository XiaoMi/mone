import type { ExtractPropTypes } from 'vue';
import type { Placement, Strategy, VirtualElement } from '@floating-ui/dom';
export declare const tourStrategies: readonly ["absolute", "fixed"];
export declare const tourPlacements: readonly ["top-start", "top-end", "top", "bottom-start", "bottom-end", "bottom", "left-start", "left-end", "left", "right-start", "right-end", "right"];
export declare const tourContentProps: {
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, string, boolean>;
    reference: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => HTMLElement | VirtualElement) | (() => HTMLElement | VirtualElement | null) | ((new (...args: any[]) => HTMLElement | VirtualElement) | (() => HTMLElement | VirtualElement | null))[], unknown, unknown, null, boolean>;
    strategy: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "fixed" | "absolute") | (() => Strategy) | ((new (...args: any[]) => "fixed" | "absolute") | (() => Strategy))[], "fixed" | "absolute", unknown, string, boolean>;
    offset: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    showArrow: BooleanConstructor;
    zIndex: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
};
export type TourContentProps = ExtractPropTypes<typeof tourContentProps>;
export declare const tourContentEmits: {
    close: () => boolean;
};
export type TourContentEmits = typeof tourContentEmits;
