import type { ExtractPropTypes } from 'vue';
export declare const basicYearTableProps: {
    readonly selectionMode: import("element-plus/es/utils").EpPropFinalized<StringConstructor, string, unknown, string, boolean>;
    readonly disabledDate: {
        readonly type: import("vue").PropType<import("./shared").DisabledDateType>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly date: {
        readonly type: import("vue").PropType<import("dayjs").Dayjs>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly minDate: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => import("dayjs").Dayjs) | (() => import("dayjs").Dayjs | null) | ((new (...args: any[]) => import("dayjs").Dayjs) | (() => import("dayjs").Dayjs | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly maxDate: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => import("dayjs").Dayjs) | (() => import("dayjs").Dayjs | null) | ((new (...args: any[]) => import("dayjs").Dayjs) | (() => import("dayjs").Dayjs | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly parsedValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => import("dayjs").Dayjs | import("dayjs").Dayjs[]) | (() => import("dayjs").Dayjs | import("dayjs").Dayjs[]) | ((new (...args: any[]) => import("dayjs").Dayjs | import("dayjs").Dayjs[]) | (() => import("dayjs").Dayjs | import("dayjs").Dayjs[]))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rangeState: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./shared").RangeState) | (() => import("./shared").RangeState) | ((new (...args: any[]) => import("./shared").RangeState) | (() => import("./shared").RangeState))[], unknown, unknown, () => {
        endDate: null;
        selecting: boolean;
    }, boolean>;
};
export type BasicYearTableProps = ExtractPropTypes<typeof basicYearTableProps>;
