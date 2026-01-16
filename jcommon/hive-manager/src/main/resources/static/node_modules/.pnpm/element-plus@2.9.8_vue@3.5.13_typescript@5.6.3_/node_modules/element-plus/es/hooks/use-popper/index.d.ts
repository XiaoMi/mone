import type { Ref } from 'vue';
import type { Instance, Modifier, Options, State, VirtualElement } from '@popperjs/core';
type ElementType = HTMLElement | undefined;
type ReferenceElement = ElementType | VirtualElement;
export type PartialOptions = Partial<Options>;
export declare const usePopper: (referenceElementRef: Ref<ReferenceElement>, popperElementRef: Ref<ElementType>, opts?: Ref<PartialOptions> | PartialOptions) => {
    state: import("vue").ComputedRef<{
        elements?: {
            reference: Element | VirtualElement;
            popper: HTMLElement;
            arrow?: HTMLElement;
        } | undefined;
        options?: import("@popperjs/core").OptionsGeneric<any> | undefined;
        placement?: import("@popperjs/core").Placement | undefined;
        strategy?: import("@popperjs/core").PositioningStrategy | undefined;
        orderedModifiers?: Modifier<any, any>[] | undefined;
        rects?: import("@popperjs/core").StateRects | undefined;
        scrollParents?: {
            reference: Array<Element | import("@popperjs/core").Window | import("@popperjs/core").VisualViewport>;
            popper: Array<Element | import("@popperjs/core").Window | import("@popperjs/core").VisualViewport>;
        } | undefined;
        styles?: {
            [key: string]: Partial<CSSStyleDeclaration>;
        } | undefined;
        attributes?: {
            [key: string]: {
                [key: string]: string | boolean;
            };
        } | undefined;
        modifiersData?: {
            [key: string]: any;
            arrow?: {
                x?: number;
                y?: number;
                centerOffset: number;
            };
            hide?: {
                isReferenceHidden: boolean;
                hasPopperEscaped: boolean;
                referenceClippingOffsets: import("@popperjs/core").SideObject;
                popperEscapeOffsets: import("@popperjs/core").SideObject;
            };
            offset?: {
                top?: import("@popperjs/core").Offsets | undefined;
                bottom?: import("@popperjs/core").Offsets | undefined;
                left?: import("@popperjs/core").Offsets | undefined;
                right?: import("@popperjs/core").Offsets | undefined;
                auto?: import("@popperjs/core").Offsets | undefined;
                "auto-start"?: import("@popperjs/core").Offsets | undefined;
                "auto-end"?: import("@popperjs/core").Offsets | undefined;
                "top-start"?: import("@popperjs/core").Offsets | undefined;
                "top-end"?: import("@popperjs/core").Offsets | undefined;
                "bottom-start"?: import("@popperjs/core").Offsets | undefined;
                "bottom-end"?: import("@popperjs/core").Offsets | undefined;
                "right-start"?: import("@popperjs/core").Offsets | undefined;
                "right-end"?: import("@popperjs/core").Offsets | undefined;
                "left-start"?: import("@popperjs/core").Offsets | undefined;
                "left-end"?: import("@popperjs/core").Offsets | undefined;
            };
            preventOverflow?: import("@popperjs/core").Offsets;
            popperOffsets?: import("@popperjs/core").Offsets;
        } | undefined;
        reset?: boolean | undefined;
    }>;
    styles: import("vue").ComputedRef<{
        [key: string]: Partial<CSSStyleDeclaration>;
    }>;
    attributes: import("vue").ComputedRef<{
        [key: string]: {
            [key: string]: string | boolean;
        };
    }>;
    update: () => Promise<Partial<State>> | undefined;
    forceUpdate: () => void | undefined;
    instanceRef: import("vue").ComputedRef<Instance | undefined>;
};
export type UsePopperReturn = ReturnType<typeof usePopper>;
export {};
