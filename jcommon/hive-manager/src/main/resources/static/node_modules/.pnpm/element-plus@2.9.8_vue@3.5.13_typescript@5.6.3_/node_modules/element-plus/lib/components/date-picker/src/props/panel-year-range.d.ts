import type { ExtractPropTypes } from 'vue';
export declare const panelYearRangeProps: {
    readonly unlinkPanels: BooleanConstructor;
    readonly parsedValue: {
        readonly type: import("vue").PropType<import("dayjs").Dayjs[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const panelYearRangeEmits: string[];
export type PanelYearRangeProps = ExtractPropTypes<typeof panelYearRangeProps>;
