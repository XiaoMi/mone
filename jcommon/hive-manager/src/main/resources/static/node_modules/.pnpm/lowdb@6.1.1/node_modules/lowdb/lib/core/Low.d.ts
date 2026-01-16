export interface Adapter<T> {
    read: () => Promise<T | null>;
    write: (data: T) => Promise<void>;
}
export interface SyncAdapter<T> {
    read: () => T | null;
    write: (data: T) => void;
}
export declare class Low<T = unknown> {
    adapter: Adapter<T>;
    data: T;
    constructor(adapter: Adapter<T>, defaultData: T);
    read(): Promise<void>;
    write(): Promise<void>;
}
export declare class LowSync<T = unknown> {
    adapter: SyncAdapter<T>;
    data: T;
    constructor(adapter: SyncAdapter<T>, defaultData: T);
    read(): void;
    write(): void;
}
