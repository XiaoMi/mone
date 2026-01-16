import { type EmitFn } from 'element-plus/es/utils';
import { type FormItemContext } from 'element-plus/es/components/form';
import type { InputTagEmits, InputTagProps } from '../input-tag';
interface UseInputTagOptions {
    props: InputTagProps;
    emit: EmitFn<InputTagEmits>;
    formItem?: FormItemContext;
}
export declare function useInputTag({ props, emit, formItem }: UseInputTagOptions): {
    inputRef: import("vue").ShallowRef<HTMLInputElement | undefined>;
    wrapperRef: import("vue").ShallowRef<HTMLElement | undefined>;
    isFocused: import("vue").Ref<boolean>;
    isComposing: import("vue").Ref<boolean>;
    inputValue: import("vue").Ref<string | undefined>;
    size: import("vue").ComputedRef<"" | "small" | "default" | "large">;
    tagSize: import("vue").ComputedRef<"default" | "small">;
    placeholder: import("vue").ComputedRef<string | undefined>;
    closable: import("vue").ComputedRef<boolean>;
    disabled: import("vue").ComputedRef<boolean>;
    inputLimit: import("vue").ComputedRef<boolean>;
    handleDragged: (draggingIndex: number, dropIndex: number, type: "before" | "after") => void;
    handleInput: (event: Event) => void;
    handleKeydown: (event: KeyboardEvent) => void;
    handleAddTag: () => void;
    handleRemoveTag: (index: number) => void;
    handleClear: () => void;
    handleCompositionStart: (event: CompositionEvent) => void;
    handleCompositionUpdate: (event: CompositionEvent) => void;
    handleCompositionEnd: (event: CompositionEvent) => void;
    focus: () => void;
    blur: () => void;
};
export {};
