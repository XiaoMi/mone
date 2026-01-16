import { PropType } from 'vue';
import { Editor, EditorConfiguration } from 'codemirror';

declare const _default: import('vue').DefineComponent<globalThis.ExtractPropTypes<{
    name: {
        type: PropType<string>;
        default: string;
    };
    value: {
        type: PropType<string>;
        default: string;
    };
    content: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => null;
    };
    placeholder: {
        type: PropType<string>;
        default: string;
    };
}>, {
    textarea: globalThis.Ref<any, any>;
    initialize: () => void;
}, {}, {}, {}, import('vue').ComponentOptionsMixin, import('vue').ComponentOptionsMixin, {
    ready: (instance: Editor) => Editor | null;
    "update:cminstance": (instance: Editor) => Editor | null;
}, string, import('vue').PublicProps, Readonly<globalThis.ExtractPropTypes<{
    name: {
        type: PropType<string>;
        default: string;
    };
    value: {
        type: PropType<string>;
        default: string;
    };
    content: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => null;
    };
    placeholder: {
        type: PropType<string>;
        default: string;
    };
}>> & Readonly<{
    onReady?: ((instance: Editor) => any) | undefined;
    "onUpdate:cminstance"?: ((instance: Editor) => any) | undefined;
}>, {
    name: string;
    value: string;
    content: string;
    options: EditorConfiguration;
    cminstance: Editor | null;
    placeholder: string;
}, {}, {}, {}, string, import('vue').ComponentProvideOptions, true, {}, any>;
export default _default;
