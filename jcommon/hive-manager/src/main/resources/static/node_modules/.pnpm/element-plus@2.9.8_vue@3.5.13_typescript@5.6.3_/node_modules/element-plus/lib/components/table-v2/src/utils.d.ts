import type { CSSProperties, Component, Slot } from 'vue';
export declare const sum: (listLike: number | number[]) => number;
export declare const tryCall: <T>(fLike: T, params: T extends (...args: infer K) => unknown ? K : any, defaultRet?: {}) => any;
export declare const enforceUnit: (style: CSSProperties) => CSSProperties;
export declare const componentToSlot: <T extends object>(ComponentLike: JSX.Element | ((props: T) => Component<T>) | undefined) => Slot | ((props: T) => import("vue").VNode<import("vue").RendererNode, import("vue").RendererElement, {
    [key: string]: any;
}>);
