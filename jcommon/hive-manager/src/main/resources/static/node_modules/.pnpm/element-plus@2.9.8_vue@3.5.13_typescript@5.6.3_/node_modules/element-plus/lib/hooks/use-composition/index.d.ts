interface UseCompositionOptions {
    afterComposition: (event: CompositionEvent) => void;
    emit?: ((event: 'compositionstart', evt: CompositionEvent) => void) & ((event: 'compositionupdate', evt: CompositionEvent) => void) & ((event: 'compositionend', evt: CompositionEvent) => void);
}
export declare function useComposition({ afterComposition, emit, }: UseCompositionOptions): {
    isComposing: import("vue").Ref<boolean>;
    handleComposition: (event: CompositionEvent) => void;
    handleCompositionStart: (event: CompositionEvent) => void;
    handleCompositionUpdate: (event: CompositionEvent) => void;
    handleCompositionEnd: (event: CompositionEvent) => void;
};
export {};
