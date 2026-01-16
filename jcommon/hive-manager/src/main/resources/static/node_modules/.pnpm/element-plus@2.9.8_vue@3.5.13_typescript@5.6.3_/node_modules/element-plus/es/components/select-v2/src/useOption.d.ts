import type { IOptionV2Props } from './token';
import type { OptionEmitFn } from './defaults';
export declare function useOption(props: IOptionV2Props, { emit }: {
    emit: OptionEmitFn;
}): {
    hoverItem: () => void;
    selectOptionClick: () => void;
};
