import type { CSSProperties, ComputedRef, Ref, SetupContext } from 'vue';
import type { SliderButtonEmits, SliderButtonInitData, SliderButtonProps } from '../button';
import type { TooltipInstance } from 'element-plus/es/components/tooltip';
export declare const useSliderButton: (props: SliderButtonProps, initData: SliderButtonInitData, emit: SetupContext<SliderButtonEmits>["emit"]) => {
    disabled: Ref<boolean>;
    button: Ref<HTMLDivElement | undefined>;
    tooltip: Ref<TooltipInstance | undefined>;
    tooltipVisible: Ref<boolean>;
    showTooltip: Ref<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
    persistent: Ref<import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>>;
    wrapperStyle: ComputedRef<CSSProperties>;
    formatValue: ComputedRef<string | number>;
    handleMouseEnter: () => void;
    handleMouseLeave: () => void;
    onButtonDown: (event: MouseEvent | TouchEvent) => void;
    onKeyDown: (event: KeyboardEvent) => void;
    setPosition: (newPosition: number) => Promise<void>;
};
