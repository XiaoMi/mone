export class WebStorage {
    #key;
    #storage;
    constructor(key, storage) {
        this.#key = key;
        this.#storage = storage;
    }
    read() {
        const value = this.#storage.getItem(this.#key);
        if (value === null) {
            return null;
        }
        return JSON.parse(value);
    }
    write(obj) {
        this.#storage.setItem(this.#key, JSON.stringify(obj));
    }
}
