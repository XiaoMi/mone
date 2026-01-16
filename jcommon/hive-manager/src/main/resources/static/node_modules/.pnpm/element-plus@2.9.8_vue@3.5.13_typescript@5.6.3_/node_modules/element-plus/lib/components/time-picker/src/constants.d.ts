export declare const timeUnits: readonly ["hours", "minutes", "seconds"];
export declare const DEFAULT_FORMATS_TIME = "HH:mm:ss";
export declare const DEFAULT_FORMATS_DATE = "YYYY-MM-DD";
export declare const DEFAULT_FORMATS_DATEPICKER: {
    date: string;
    dates: string;
    week: string;
    year: string;
    years: string;
    month: string;
    months: string;
    datetime: string;
    monthrange: string;
    yearrange: string;
    daterange: string;
    datetimerange: string;
};
export type TimeUnit = typeof timeUnits[number];
