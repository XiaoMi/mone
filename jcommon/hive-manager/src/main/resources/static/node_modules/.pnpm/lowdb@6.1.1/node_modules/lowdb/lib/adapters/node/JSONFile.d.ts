/// <reference types="node" resolution-mode="require"/>
import { PathLike } from 'fs';
import { Adapter, SyncAdapter } from '../../core/Low.js';
export declare class JSONFile<T> implements Adapter<T> {
    #private;
    constructor(filename: PathLike);
    read(): Promise<T | null>;
    write(obj: T): Promise<void>;
}
export declare class JSONFileSync<T> implements SyncAdapter<T> {
    #private;
    constructor(filename: PathLike);
    read(): T | null;
    write(obj: T): void;
}
