package com.jbase;

import com.jbase.core.index.BTree;
import com.jbase.core.kv.KVStore;
import com.jbase.core.storage.MemStore;

public class Main {

    public static void main(String[] args) {
        MemStore store = new MemStore();
        BTree tree = new BTree(store);
        KVStore db = new KVStore(tree);

        db.put("a".getBytes(), "1".getBytes());
        db.put("b".getBytes(), "2".getBytes());
        db.put("c".getBytes(), "3".getBytes());
        db.put("d".getBytes(), "4".getBytes());
        db.put("e".getBytes(), "5".getBytes()); // should trigger splits

        byte[] v;

        v = db.get("a".getBytes());
        System.out.println(v == null ? "null" : new String(v));

        v = db.get("c".getBytes());
        System.out.println(v == null ? "null" : new String(v));

        v = db.get("e".getBytes());
        System.out.println(v == null ? "null" : new String(v));

        v = db.get("z".getBytes());
        System.out.println(v == null ? "null" : new String(v));

    }
}
