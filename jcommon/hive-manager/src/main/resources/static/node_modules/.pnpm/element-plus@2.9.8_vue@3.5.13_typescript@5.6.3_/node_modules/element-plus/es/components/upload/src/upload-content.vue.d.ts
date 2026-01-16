import type { UploadFile, UploadRawFile } from './upload';
declare function __VLS_template(): {
    default?(_: {}): any;
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly beforeUpload: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | (() => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | {
        (): (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | (() => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | {
        (): (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onRemove: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | (() => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | {
        (): (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | (() => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | {
        (): (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onStart: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (rawFile: UploadRawFile) => void) | (() => (rawFile: UploadRawFile) => void) | {
        (): (rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (rawFile: UploadRawFile) => void) | (() => (rawFile: UploadRawFile) => void) | {
        (): (rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onSuccess: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (response: any, rawFile: UploadRawFile) => unknown) | (() => (response: any, rawFile: UploadRawFile) => unknown) | {
        (): (response: any, rawFile: UploadRawFile) => unknown;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (response: any, rawFile: UploadRawFile) => unknown) | (() => (response: any, rawFile: UploadRawFile) => unknown) | {
        (): (response: any, rawFile: UploadRawFile) => unknown;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onProgress: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | (() => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | {
        (): (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | (() => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | {
        (): (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onError: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | (() => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | {
        (): (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | (() => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | {
        (): (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onExceed: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | (() => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | {
        (): (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | (() => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | {
        (): (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly action: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "#", boolean>;
    readonly headers: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Record<string, any> | Headers) | (() => Record<string, any> | Headers) | ((new (...args: any[]) => Record<string, any> | Headers) | (() => Record<string, any> | Headers))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly method: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "post", boolean>;
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | ((new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly multiple: BooleanConstructor;
    readonly name: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "file", boolean>;
    readonly drag: BooleanConstructor;
    readonly withCredentials: BooleanConstructor;
    readonly showFileList: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly accept: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly fileList: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadUserFile[]) | (() => import("./upload").UploadUserFile[]) | ((new (...args: any[]) => import("./upload").UploadUserFile[]) | (() => import("./upload").UploadUserFile[]))[], unknown, unknown, () => [], boolean>;
    readonly autoUpload: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly listType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "picture" | "text" | "picture-card", unknown, "text", boolean>;
    readonly httpRequest: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadRequestHandler) | (() => import("./upload").UploadRequestHandler) | {
        (): import("./upload").UploadRequestHandler;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => import("./upload").UploadRequestHandler) | (() => import("./upload").UploadRequestHandler) | {
        (): import("./upload").UploadRequestHandler;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, import("./upload").UploadRequestHandler, boolean>;
    readonly disabled: BooleanConstructor;
    readonly limit: NumberConstructor;
}, {
    abort: (file?: UploadFile) => void;
    upload: (rawFile: UploadRawFile) => Promise<void>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly beforeUpload: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | (() => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | {
        (): (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | (() => (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>) | {
        (): (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onRemove: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | (() => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | {
        (): (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | (() => (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void) | {
        (): (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onStart: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (rawFile: UploadRawFile) => void) | (() => (rawFile: UploadRawFile) => void) | {
        (): (rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (rawFile: UploadRawFile) => void) | (() => (rawFile: UploadRawFile) => void) | {
        (): (rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onSuccess: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (response: any, rawFile: UploadRawFile) => unknown) | (() => (response: any, rawFile: UploadRawFile) => unknown) | {
        (): (response: any, rawFile: UploadRawFile) => unknown;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (response: any, rawFile: UploadRawFile) => unknown) | (() => (response: any, rawFile: UploadRawFile) => unknown) | {
        (): (response: any, rawFile: UploadRawFile) => unknown;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onProgress: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | (() => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | {
        (): (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | (() => (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void) | {
        (): (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onError: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | (() => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | {
        (): (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | (() => (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void) | {
        (): (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly onExceed: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | (() => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | {
        (): (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | (() => (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void) | {
        (): (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, () => void, boolean>;
    readonly action: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "#", boolean>;
    readonly headers: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Record<string, any> | Headers) | (() => Record<string, any> | Headers) | ((new (...args: any[]) => Record<string, any> | Headers) | (() => Record<string, any> | Headers))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly method: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "post", boolean>;
    readonly data: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | ((new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)))[], unknown, unknown, () => import("element-plus/es/utils").Mutable<{}>, boolean>;
    readonly multiple: BooleanConstructor;
    readonly name: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "file", boolean>;
    readonly drag: BooleanConstructor;
    readonly withCredentials: BooleanConstructor;
    readonly showFileList: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly accept: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly fileList: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadUserFile[]) | (() => import("./upload").UploadUserFile[]) | ((new (...args: any[]) => import("./upload").UploadUserFile[]) | (() => import("./upload").UploadUserFile[]))[], unknown, unknown, () => [], boolean>;
    readonly autoUpload: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly listType: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "picture" | "text" | "picture-card", unknown, "text", boolean>;
    readonly httpRequest: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadRequestHandler) | (() => import("./upload").UploadRequestHandler) | {
        (): import("./upload").UploadRequestHandler;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => import("./upload").UploadRequestHandler) | (() => import("./upload").UploadRequestHandler) | {
        (): import("./upload").UploadRequestHandler;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, import("./upload").UploadRequestHandler, boolean>;
    readonly disabled: BooleanConstructor;
    readonly limit: NumberConstructor;
}>>, {
    readonly data: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | ((new (...args: any[]) => import("element-plus/es/utils").Mutable<Record<string, any>> | Promise<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)) | (() => import("element-plus/es/utils").Awaitable<import("element-plus/es/utils").Mutable<Record<string, any>>> | ((rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<import("./upload").UploadData>)))[], unknown, unknown>;
    readonly disabled: boolean;
    readonly drag: boolean;
    readonly multiple: boolean;
    readonly name: string;
    readonly onError: (err: import("./ajax.js").UploadAjaxError, rawFile: UploadRawFile) => void;
    readonly onProgress: (evt: import("./upload").UploadProgressEvent, rawFile: UploadRawFile) => void;
    readonly action: string;
    readonly withCredentials: boolean;
    readonly method: string;
    readonly showFileList: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly accept: string;
    readonly fileList: import("./upload").UploadUserFile[];
    readonly autoUpload: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly listType: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "picture" | "text" | "picture-card", unknown>;
    readonly httpRequest: import("./upload").UploadRequestHandler;
    readonly beforeUpload: (rawFile: UploadRawFile) => import("element-plus/es/utils").Awaitable<void | undefined | null | boolean | File | Blob>;
    readonly onRemove: (file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => void;
    readonly onSuccess: (response: any, rawFile: UploadRawFile) => unknown;
    readonly onExceed: (files: File[], uploadFiles: import("./upload").UploadUserFile[]) => void;
    readonly onStart: (rawFile: UploadRawFile) => void;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
