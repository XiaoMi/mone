import { Adapter, SyncAdapter } from '../core/Low.js';
export declare class Memory<T> implements Adapter<T> {
    #private;
    read(): Promise<T | null>;
    write(obj: T): Promise<void>;
}
export declare class MemorySync<T> implements SyncAdapter<T> {
    #private;
    read(): T | null;
    write(obj: T): void;
}
