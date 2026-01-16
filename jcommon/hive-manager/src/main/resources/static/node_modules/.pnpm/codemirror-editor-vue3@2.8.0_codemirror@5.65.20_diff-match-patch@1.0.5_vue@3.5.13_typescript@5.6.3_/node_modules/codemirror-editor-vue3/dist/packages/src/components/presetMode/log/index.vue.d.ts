import { PropType } from 'vue';
import { Editor } from 'codemirror';

declare const _default: import('vue').DefineComponent<globalThis.ExtractPropTypes<{
    value: {
        type: PropType<string>;
        default: string;
    };
    name: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: ObjectConstructor;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => {};
    };
    placeholder: {
        type: StringConstructor;
        default: string;
    };
}>, {
    initialize: () => void;
    textarea: globalThis.Ref<any, any>;
}, {}, {}, {}, import('vue').ComponentOptionsMixin, import('vue').ComponentOptionsMixin, ("ready" | "update:cminstance")[], "ready" | "update:cminstance", import('vue').PublicProps, Readonly<globalThis.ExtractPropTypes<{
    value: {
        type: PropType<string>;
        default: string;
    };
    name: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: ObjectConstructor;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => {};
    };
    placeholder: {
        type: StringConstructor;
        default: string;
    };
}>> & Readonly<{
    onReady?: ((...args: any[]) => any) | undefined;
    "onUpdate:cminstance"?: ((...args: any[]) => any) | undefined;
}>, {
    name: string;
    value: string;
    options: Record<string, any>;
    cminstance: Editor | null;
    placeholder: string;
}, {}, {}, {}, string, import('vue').ComponentProvideOptions, true, {}, any>;
export default _default;
