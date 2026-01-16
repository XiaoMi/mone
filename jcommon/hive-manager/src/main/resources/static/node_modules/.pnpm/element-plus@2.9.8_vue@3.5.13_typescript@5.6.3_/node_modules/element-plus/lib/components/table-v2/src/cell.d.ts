import type { ExtractPropTypes, StyleValue } from 'vue';
export declare const tableV2CellProps: {
    readonly class: StringConstructor;
    readonly cellData: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly column: {
        readonly type: import("vue").PropType<import("./common").AnyColumn>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly columnIndex: NumberConstructor;
    readonly style: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowData: {
        readonly type: import("vue").PropType<any>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly rowIndex: NumberConstructor;
};
export type TableV2CellProps = ExtractPropTypes<typeof tableV2CellProps>;
