import type { Ref } from 'vue';
type ModalInstance = {
    handleClose: () => void;
};
export declare const useModal: (instance: ModalInstance, visibleRef: Ref<boolean>) => void;
export {};
