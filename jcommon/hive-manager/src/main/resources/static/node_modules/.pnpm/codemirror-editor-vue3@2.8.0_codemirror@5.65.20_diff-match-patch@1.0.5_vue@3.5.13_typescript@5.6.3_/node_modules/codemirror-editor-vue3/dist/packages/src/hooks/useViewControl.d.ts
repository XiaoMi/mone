import { Ref } from 'vue';
import { Editor } from 'codemirror';
import { MergeView } from 'codemirror/addon/merge/merge';
import { CmProps } from '../types/props';

export declare type UseViewControlParams = {
    props: CmProps;
    cminstance: Ref<Editor | MergeView | null>;
    presetRef: Ref<{
        initialize: () => void;
    } | null>;
};
export declare function useViewControl({ props, cminstance, presetRef }: UseViewControlParams): {
    reload: () => void;
    refresh: () => void;
    resize: (width?: string | number | null, height?: string | number | null) => void;
    destroy: () => void;
    containerWidth: Ref<string, string>;
    containerHeight: Ref<string, string>;
    reviseStyle: () => void;
};
