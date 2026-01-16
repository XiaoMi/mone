export class Memory {
    #data = null;
    read() {
        return Promise.resolve(this.#data);
    }
    write(obj) {
        this.#data = obj;
        return Promise.resolve();
    }
}
export class MemorySync {
    #data = null;
    read() {
        return this.#data || null;
    }
    write(obj) {
        this.#data = obj;
    }
}
