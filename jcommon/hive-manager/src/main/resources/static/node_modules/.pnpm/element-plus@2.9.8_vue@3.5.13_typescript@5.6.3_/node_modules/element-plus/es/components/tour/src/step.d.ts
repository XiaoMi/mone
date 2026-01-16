import type { CSSProperties, ExtractPropTypes } from 'vue';
import type { TourBtnProps, TourMask } from './types';
export declare const tourStepProps: {
    target: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | HTMLElement | (() => HTMLElement | null)) | (() => string | HTMLElement | (() => HTMLElement | null) | null) | ((new (...args: any[]) => string | HTMLElement | (() => HTMLElement | null)) | (() => string | HTMLElement | (() => HTMLElement | null) | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    title: StringConstructor;
    description: StringConstructor;
    showClose: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    closeIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    showArrow: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => import("@floating-ui/core").Placement))[], "top" | "bottom" | "left" | "right" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end", unknown, string, boolean>;
    mask: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => boolean | {
        style?: CSSProperties;
        color?: string;
    }) | (() => TourMask) | ((new (...args: any[]) => boolean | {
        style?: CSSProperties;
        color?: string;
    }) | (() => TourMask))[], unknown, unknown, undefined, boolean>;
    contentStyle: {
        readonly type: import("vue").PropType<CSSProperties>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    prevButtonProps: {
        readonly type: import("vue").PropType<TourBtnProps>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    nextButtonProps: {
        readonly type: import("vue").PropType<TourBtnProps>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    scrollIntoViewOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => boolean | ScrollIntoViewOptions) | (() => boolean | ScrollIntoViewOptions) | ((new (...args: any[]) => boolean | ScrollIntoViewOptions) | (() => boolean | ScrollIntoViewOptions))[], unknown, unknown, undefined, boolean>;
    type: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "default" | "primary") | (() => "default" | "primary") | ((new (...args: any[]) => "default" | "primary") | (() => "default" | "primary"))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type TourStepProps = ExtractPropTypes<typeof tourStepProps>;
export declare const tourStepEmits: {
    close: () => boolean;
};
export type TourStepEmits = typeof tourStepEmits;
