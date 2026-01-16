export declare const useScrollbar: () => {
    scrollBarRef: import("vue").Ref<any>;
    scrollTo: (options: ScrollToOptions | number, yCoord?: number) => void;
    setScrollTop: (top?: number) => void;
    setScrollLeft: (left?: number) => void;
};
