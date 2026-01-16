import { TextFile, TextFileSync } from './TextFile.js';
export class JSONFile {
    #adapter;
    constructor(filename) {
        this.#adapter = new TextFile(filename);
    }
    async read() {
        const data = await this.#adapter.read();
        if (data === null) {
            return null;
        }
        else {
            return JSON.parse(data);
        }
    }
    write(obj) {
        return this.#adapter.write(JSON.stringify(obj, null, 2));
    }
}
export class JSONFileSync {
    #adapter;
    constructor(filename) {
        this.#adapter = new TextFileSync(filename);
    }
    read() {
        const data = this.#adapter.read();
        if (data === null) {
            return null;
        }
        else {
            return JSON.parse(data);
        }
    }
    write(obj) {
        this.#adapter.write(JSON.stringify(obj, null, 2));
    }
}
