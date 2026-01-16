import { SyncAdapter } from '../../core/Low.js';
export declare class WebStorage<T> implements SyncAdapter<T> {
    #private;
    constructor(key: string, storage: Storage);
    read(): T | null;
    write(obj: T): void;
}
