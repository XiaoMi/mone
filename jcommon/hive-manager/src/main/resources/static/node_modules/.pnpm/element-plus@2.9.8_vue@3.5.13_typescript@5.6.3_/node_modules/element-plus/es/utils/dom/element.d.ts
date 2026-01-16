type GetElement = <T extends string | HTMLElement | Window | null | undefined>(target: T) => T extends string ? HTMLElement | null : T;
export declare const getElement: GetElement;
export {};
