import { type ShallowRef } from 'vue';
type DropType = 'before' | 'after';
interface UseDragTagOptions {
    wrapperRef: ShallowRef<HTMLElement | undefined>;
    handleDragged: (draggingIndex: number, dropIndex: number, type: DropType) => void;
    afterDragged?: () => void;
}
export declare function useDragTag({ wrapperRef, handleDragged, afterDragged, }: UseDragTagOptions): {
    dropIndicatorRef: ShallowRef<HTMLElement | undefined>;
    showDropIndicator: import("vue").Ref<boolean>;
    handleDragStart: (event: DragEvent, index: number) => void;
    handleDragOver: (event: DragEvent, index: number) => void;
    handleDragEnd: (event: DragEvent) => void;
};
export {};
