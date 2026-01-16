declare function __VLS_template(): {
    header?(_: {
        date: string;
    }): any;
    "date-cell"?(_: {
        data: {
            isSelected: boolean;
            type: string;
            day: string;
            date: Date;
        };
    }): any;
    "date-cell"?(_: {
        data: {
            isSelected: boolean;
            type: string;
            day: string;
            date: Date;
        };
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly modelValue: {
        readonly type: import("vue").PropType<Date>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly range: {
        readonly type: import("vue").PropType<[Date, Date]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, {
    /** @description currently selected date */
    selectedDay: import("vue").WritableComputedRef<import("dayjs").Dayjs | undefined>;
    /** @description select a specific date */
    pickDay: (day: import("dayjs").Dayjs) => void;
    /** @description select date */
    selectDate: (type: import("./calendar").CalendarDateType) => void;
    /** @description Calculate the validate date range according to the start and end dates */
    calculateValidatedDateRange: (startDayjs: import("dayjs").Dayjs, endDayjs: import("dayjs").Dayjs) => [import("dayjs").Dayjs, import("dayjs").Dayjs][];
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: Date) => void;
    input: (value: Date) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly modelValue: {
        readonly type: import("vue").PropType<Date>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly range: {
        readonly type: import("vue").PropType<[Date, Date]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>> & {
    "onUpdate:modelValue"?: ((value: Date) => any) | undefined;
    onInput?: ((value: Date) => any) | undefined;
}, {}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
