import type { UploadFile } from './upload';
declare function __VLS_template(): {
    default?(_: {
        file: UploadFile;
        index: number;
    }): any;
    append?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly files: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadFiles) | (() => import("./upload").UploadFiles) | ((new (...args: any[]) => import("./upload").UploadFiles) | (() => import("./upload").UploadFiles))[], unknown, unknown, () => never[], boolean>;
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
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    remove: (file: UploadFile) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly files: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./upload").UploadFiles) | (() => import("./upload").UploadFiles) | ((new (...args: any[]) => import("./upload").UploadFiles) | (() => import("./upload").UploadFiles))[], unknown, unknown, () => never[], boolean>;
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
}>> & {
    onRemove?: ((file: UploadFile) => any) | undefined;
}, {
    readonly disabled: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly listType: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "picture" | "text" | "picture-card", unknown>;
    readonly files: import("./upload").UploadFiles;
    readonly handlePreview: (uploadFile: UploadFile) => void;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
