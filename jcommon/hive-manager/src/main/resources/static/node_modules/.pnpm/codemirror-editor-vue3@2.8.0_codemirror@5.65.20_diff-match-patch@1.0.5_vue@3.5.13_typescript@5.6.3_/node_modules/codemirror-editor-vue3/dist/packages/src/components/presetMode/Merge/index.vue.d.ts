import { Editor, EditorConfiguration } from 'codemirror';
import { PropType } from 'vue';

declare const _default: import('vue').DefineComponent<globalThis.ExtractPropTypes<{
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => {};
    };
}>, {
    mergeView: globalThis.Ref<HTMLElement | undefined, HTMLElement | undefined>;
    initialize: () => void;
}, {}, {}, {}, import('vue').ComponentOptionsMixin, import('vue').ComponentOptionsMixin, ("ready" | "update:cminstance")[], "ready" | "update:cminstance", import('vue').PublicProps, Readonly<globalThis.ExtractPropTypes<{
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {};
    };
    cminstance: {
        type: PropType<Editor | null>;
        default: () => {};
    };
}>> & Readonly<{
    onReady?: ((...args: any[]) => any) | undefined;
    "onUpdate:cminstance"?: ((...args: any[]) => any) | undefined;
}>, {
    options: EditorConfiguration;
    cminstance: Editor | null;
}, {}, {}, {}, string, import('vue').ComponentProvideOptions, true, {}, any>;
export default _default;
