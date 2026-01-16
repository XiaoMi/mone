/** An event emitted by the child process. */
export type Event = StdoutEvent | StderrEvent | ExitEvent;
/** An event sent from the worker to the host. */
export type InternalEvent = InternalStdoutEvent | InternalStderrEvent | ExitEvent | ErrorEvent;
/** An event indicating that data has been emitted over stdout. */
export interface StdoutEvent {
    type: 'stdout';
    data: Buffer;
}
/** An event indicating that data has been emitted over stderr. */
export interface StderrEvent {
    type: 'stderr';
    data: Buffer;
}
/** An event indicating that process has exited. */
export interface ExitEvent {
    type: 'exit';
    /**
     * The exit code. This will be `undefined` if the subprocess was killed via
     * signal.
     */
    code?: number;
    /**
     * The signal that caused this process to exit. This will be `undefined` if
     * the subprocess exited normally.
     */
    signal?: NodeJS.Signals;
}
/**
 * The stdout event sent from the worker to the host. The structured clone
 * algorithm automatically converts `Buffer`s sent through `MessagePort`s to
 * `Uint8Array`s.
 */
export interface InternalStdoutEvent {
    type: 'stdout';
    data: Buffer | Uint8Array;
}
/**
 * The stderr event sent from the worker to the host. The structured clone
 * algorithm automatically converts `Buffer`s sent through `MessagePort`s to
 * `Uint8Array`s.
 */
export interface InternalStderrEvent {
    type: 'stderr';
    data: Buffer | Uint8Array;
}
/**
 * An error occurred when starting or closing the child process. This is only
 * used internally; the host will throw the error rather than returning it to
 * the caller.
 */
export interface ErrorEvent {
    type: 'error';
    error: Error;
}
