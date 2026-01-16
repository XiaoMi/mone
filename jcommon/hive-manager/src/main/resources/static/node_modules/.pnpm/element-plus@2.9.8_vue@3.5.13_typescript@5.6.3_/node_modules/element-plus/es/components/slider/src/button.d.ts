import type { ComponentPublicInstance, ExtractPropTypes, Ref } from 'vue';
import type Button from './button.vue';
export declare const sliderButtonProps: {
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly vertical: BooleanConstructor;
    readonly tooltipClass: StringConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, import("@popperjs/core").Placement, unknown, "top", boolean>;
};
export type SliderButtonProps = ExtractPropTypes<typeof sliderButtonProps>;
export declare const sliderButtonEmits: {
    "update:modelValue": (value: number) => boolean;
};
export type SliderButtonEmits = typeof sliderButtonEmits;
export type SliderButtonInstance = ComponentPublicInstance<typeof Button>;
export type ButtonRefs = Record<'firstButton' | 'secondButton', Ref<SliderButtonInstance | undefined>>;
export interface SliderButtonInitData {
    hovering: boolean;
    dragging: boolean;
    isClick: boolean;
    startX: number;
    currentX: number;
    startY: number;
    currentY: number;
    startPosition: number;
    newPosition: number;
    oldValue: number;
}
