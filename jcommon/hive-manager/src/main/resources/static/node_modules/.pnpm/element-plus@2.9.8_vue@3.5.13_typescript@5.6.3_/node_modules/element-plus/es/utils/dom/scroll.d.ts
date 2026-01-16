export declare const isScroll: (el: HTMLElement, isVertical?: boolean) => boolean;
export declare const getScrollContainer: (el: HTMLElement, isVertical?: boolean) => Window | HTMLElement | undefined;
export declare const getScrollBarWidth: (namespace: string) => number;
/**
 * Scroll with in the container element, positioning the **selected** element at the top
 * of the container
 */
export declare function scrollIntoView(container: HTMLElement, selected: HTMLElement): void;
export declare function animateScrollTo(container: HTMLElement | Window, from: number, to: number, duration: number, callback?: unknown): () => void;
export declare const getScrollElement: (target: HTMLElement, container: HTMLElement | Window) => HTMLElement;
export declare const getScrollTop: (container: HTMLElement | Window) => number;
