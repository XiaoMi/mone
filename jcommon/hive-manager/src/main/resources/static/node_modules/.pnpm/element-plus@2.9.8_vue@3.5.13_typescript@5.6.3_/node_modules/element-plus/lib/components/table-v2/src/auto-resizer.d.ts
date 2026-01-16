import type { ExtractPropTypes } from 'vue';
type AutoResizeHandler = (event: {
    height: number;
    width: number;
}) => void;
export declare const autoResizerProps: {
    readonly disableWidth: BooleanConstructor;
    readonly disableHeight: BooleanConstructor;
    readonly onResize: {
        readonly type: import("vue").PropType<AutoResizeHandler>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type AutoResizerProps = ExtractPropTypes<typeof autoResizerProps>;
export {};
