import type { CSSProperties, Component, InjectionKey, Ref, SetupContext } from 'vue';
import type { UseNamespaceReturn } from 'element-plus/es/hooks';
import type { TourGap, TourMask } from './types';
import type { Placement, Strategy, VirtualElement } from '@floating-ui/dom';
import type { TourStepProps } from './step';
export declare const useTarget: (target: Ref<string | HTMLElement | (() => HTMLElement | null) | null | undefined>, open: Ref<boolean>, gap: Ref<TourGap>, mergedMask: Ref<TourMask>, scrollIntoViewOptions: Ref<boolean | ScrollIntoViewOptions>) => {
    mergedPosInfo: import("vue").ComputedRef<{
        left: number;
        top: number;
        width: number;
        height: number;
        radius: number;
    } | null>;
    triggerTarget: import("vue").ComputedRef<HTMLElement | {
        getBoundingClientRect(): DOMRect;
    } | undefined>;
};
export interface TourContext {
    currentStep: Ref<TourStepProps | undefined>;
    current: Ref<number>;
    total: Ref<number>;
    showClose: Ref<boolean>;
    closeIcon: Ref<string | Component>;
    mergedType: Ref<'default' | 'primary' | undefined>;
    ns: UseNamespaceReturn;
    slots: SetupContext['slots'];
    updateModelValue(modelValue: boolean): void;
    onClose(): void;
    onFinish(): void;
    onChange(): void;
}
export declare const tourKey: InjectionKey<TourContext>;
export declare const useFloating: (referenceRef: Ref<HTMLElement | VirtualElement | null>, contentRef: Ref<HTMLElement | null>, arrowRef: Ref<HTMLElement | null>, placement: Ref<Placement | undefined>, strategy: Ref<Strategy>, offset: Ref<number>, zIndex: Ref<number>, showArrow: Ref<boolean>) => {
    update: () => Promise<void>;
    contentStyle: import("vue").ComputedRef<CSSProperties>;
    arrowStyle: import("vue").ComputedRef<CSSProperties>;
};
