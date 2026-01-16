import type { CSSProperties } from 'vue';
import type { Direction, RTLOffsetType } from './types';
export declare const getScrollDir: (prev: number, cur: number) => "forward" | "backward";
export declare const isHorizontal: (dir: string) => dir is "ltr" | "rtl" | "horizontal";
export declare const isRTL: (dir: Direction) => dir is "rtl";
export declare function getRTLOffsetType(recalculate?: boolean): RTLOffsetType;
type RenderThumbStyleParams = {
    bar: {
        size: 'height' | 'width';
        axis: 'X' | 'Y';
    };
    size: string;
    move: number;
};
export declare function renderThumbStyle({ move, size, bar }: RenderThumbStyleParams, layout: string): CSSProperties;
export {};
