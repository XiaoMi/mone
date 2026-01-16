export declare const ariaProps: {
    ariaLabel: StringConstructor;
    ariaOrientation: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    ariaControls: StringConstructor;
};
export declare const useAriaProps: <T extends keyof typeof ariaProps>(arias: Array<T>) => Pick<{
    ariaLabel: StringConstructor;
    ariaOrientation: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    ariaControls: StringConstructor;
}, T>;
