import * as stream from 'stream';
import { ExitEvent, StderrEvent, StdoutEvent } from './event';
export { ExitEvent, StderrEvent, StdoutEvent } from './event';
/**
 * A child process that runs synchronously while also allowing the user to
 * interact with it before it shuts down.
 */
export declare class SyncChildProcess implements Iterator<StderrEvent | StdoutEvent, ExitEvent | undefined> {
    /** The port that communicates with the worker thread. */
    private readonly port;
    /** The worker in which the child process runs. */
    private readonly worker;
    /** The standard input stream to write to the process. */
    readonly stdin: stream.Writable;
    /** Creates a new synchronous process running `command` with `args`. */
    constructor(command: string, options?: Options);
    constructor(command: string, args: string[], options?: Options);
    /**
     * Blocks until the child process is ready to emit another event, then returns
     * that event. This will return an [IteratorReturnResult] with an [ExitEvent]
     * once when the process exits. If it's called again after that, it will
     * return `{done: true}` without a value.
     *
     * If there's an error running the child process, this will throw that error.
     */
    next(): IteratorResult<StdoutEvent | StderrEvent, ExitEvent | undefined>;
    /**
     * Sends a signal (`SIGTERM` by default) to the child process.
     *
     * This has no effect if the process has already exited.
     */
    kill(signal?: NodeJS.Signals | number): void;
    /** Closes down the worker thread and the stdin stream. */
    private close;
}
/**
 * A subset of the options for [`child_process.spawn()`].
 *
 * [`child_process.spawn()`]: https://nodejs.org/api/child_process.html#child_processspawncommand-args-options
 */
export interface Options {
    cwd?: string;
    env?: Record<string, string>;
    argv0?: string;
    uid?: number;
    gid?: number;
    shell?: boolean | string;
    windowsVerbatimArguments?: boolean;
    windowsHide?: boolean;
    timeout?: number;
    killSignal?: string | number;
}
