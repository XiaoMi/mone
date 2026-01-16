import type { ExtractPropTypes } from 'vue';
export declare const tableV2HeaderCell: {
    class: StringConstructor;
    columnIndex: NumberConstructor;
    column: {
        readonly type: import("vue").PropType<import("./common").AnyColumn>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type TableV2HeaderCell = ExtractPropTypes<typeof tableV2HeaderCell>;
