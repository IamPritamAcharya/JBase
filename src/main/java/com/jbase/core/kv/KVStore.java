package com.jbase.core.kv;

import com.jbase.core.index.BTree;

public class KVStore {

    private final BTree tree;

    public KVStore(BTree tree) {
        this.tree = tree;
    }

    public void put(byte[] key, byte[] value) {
        tree.insert(key, value);
    }

    public byte[] get(byte[] key) {
        return tree.search(key);
    }

    public void delete(byte[] key) {
        tree.insert(key, null);
    }

}
