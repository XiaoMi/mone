import type { ExtractPropTypes } from 'vue';
import type { UploadFile, UploadFiles } from './upload';
import type UploadList from './upload-list.vue';
export declare const uploadListProps: {
    readonly files: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => UploadFiles) | (() => UploadFiles) | ((new (...args: any[]) => UploadFiles) | (() => UploadFiles))[], unknown, unknown, () => never[], boolean>;
    readonly disabled: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, false, boolean>;
    readonly handlePreview: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (uploadFile: UploadFile) => void) | (() => (uploadFile: UploadFile) => void) | {
        (): (uploadFile: UploadFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (uploadFile: UploadFile) => void) | (() => (uploadFile: UploadFile) => void) | {
        (): (uploadFile: UploadFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly listType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "picture" | "text" | "picture-card", unknown, "text", boolean>;
    readonly crossorigin: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "" | "anonymous" | "use-credentials") | (() => "" | "anonymous" | "use-credentials") | ((new (...args: any[]) => "" | "anonymous" | "use-credentials") | (() => "" | "anonymous" | "use-credentials"))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type UploadListProps = ExtractPropTypes<typeof uploadListProps>;
export declare const uploadListEmits: {
    remove: (file: UploadFile) => boolean;
};
export type UploadListEmits = typeof uploadListEmits;
export type UploadListInstance = InstanceType<typeof UploadList> & unknown;
