export declare function throttleByRaf(cb: (...args: any[]) => void): {
    (...args: any[]): void;
    cancel(): void;
};
