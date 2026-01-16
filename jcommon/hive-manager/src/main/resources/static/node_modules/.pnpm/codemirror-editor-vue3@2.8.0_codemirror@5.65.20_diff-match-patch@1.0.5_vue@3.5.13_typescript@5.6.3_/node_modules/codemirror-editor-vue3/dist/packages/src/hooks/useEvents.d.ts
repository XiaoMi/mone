import { Editor } from 'codemirror';
import { ComponentInternalInstance, Ref } from 'vue';
import { MergeView } from 'codemirror/addon/merge/merge';
import { CmProps } from '../types/props';

declare type UseEventsParams = {
    props: CmProps;
    cminstance: Ref<Editor | MergeView>;
    emit: ((event: "ready", cm: Editor) => void) & ((event: "update:value", value: string) => void) & ((event: "change", value: string, cm: Editor) => void) & ((event: "input", value: string) => void) & ((event: string, ...args: any[]) => void);
    internalInstance: ComponentInternalInstance | null;
    content: Ref<string>;
};
export declare function scrollToEnd(cm: Editor): void;
declare const useEvents: ({ props, cminstance, emit, internalInstance, content, }: UseEventsParams) => {
    listenerEvents: () => void;
};
export { useEvents };
