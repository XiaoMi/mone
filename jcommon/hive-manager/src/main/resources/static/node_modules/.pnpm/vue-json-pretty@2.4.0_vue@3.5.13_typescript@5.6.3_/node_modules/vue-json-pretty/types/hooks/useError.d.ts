declare type UseErrorOptions = {
    emitListener: boolean;
};
export default function useError(message: string, { emitListener }: UseErrorOptions): {
    emit: () => never;
};
export {};
