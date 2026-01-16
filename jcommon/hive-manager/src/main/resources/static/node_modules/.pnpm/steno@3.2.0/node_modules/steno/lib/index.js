import { rename, writeFile } from 'node:fs/promises';
import { basename, dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
// Returns a temporary file
// Example: for /some/file will return /some/.file.tmp
function getTempFilename(file) {
    const f = file instanceof URL ? fileURLToPath(file) : file.toString();
    return join(dirname(f), `.${basename(f)}.tmp`);
}
export class Writer {
    #filename;
    #tempFilename;
    #locked = false;
    #prev = null;
    #next = null;
    #nextPromise = null;
    #nextData = null;
    // File is locked, add data for later
    #add(data) {
        // Only keep most recent data
        this.#nextData = data;
        // Create a singleton promise to resolve all next promises once next data is written
        this.#nextPromise ||= new Promise((resolve, reject) => {
            this.#next = [resolve, reject];
        });
        // Return a promise that will resolve at the same time as next promise
        return new Promise((resolve, reject) => {
            this.#nextPromise?.then(resolve).catch(reject);
        });
    }
    // File isn't locked, write data
    async #write(data) {
        // Lock file
        this.#locked = true;
        try {
            // Atomic write
            await writeFile(this.#tempFilename, data, 'utf-8');
            await rename(this.#tempFilename, this.#filename);
            // Call resolve
            this.#prev?.[0]();
        }
        catch (err) {
            // Call reject
            if (err instanceof Error) {
                this.#prev?.[1](err);
            }
            throw err;
        }
        finally {
            // Unlock file
            this.#locked = false;
            this.#prev = this.#next;
            this.#next = this.#nextPromise = null;
            if (this.#nextData !== null) {
                const nextData = this.#nextData;
                this.#nextData = null;
                await this.write(nextData);
            }
        }
    }
    constructor(filename) {
        this.#filename = filename;
        this.#tempFilename = getTempFilename(filename);
    }
    async write(data) {
        return this.#locked ? this.#add(data) : this.#write(data);
    }
}
