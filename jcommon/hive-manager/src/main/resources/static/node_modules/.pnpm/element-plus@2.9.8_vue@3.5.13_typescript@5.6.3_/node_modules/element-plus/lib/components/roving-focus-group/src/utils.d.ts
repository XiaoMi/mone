import type { HTMLAttributes } from 'vue';
type Orientation = HTMLAttributes['aria-orientation'];
type Direction = 'ltr' | 'rtl';
type FocusIntent = 'first' | 'last' | 'prev' | 'next';
export declare const getFocusIntent: (event: KeyboardEvent, orientation?: Orientation, dir?: Direction) => FocusIntent | undefined;
export declare const reorderArray: <T>(array: T[], atIdx: number) => T[];
export declare const focusFirst: (elements: HTMLElement[]) => void;
export {};
