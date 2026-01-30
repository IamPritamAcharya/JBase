package com.jbase;

import com.jbase.core.index.BTree;
import com.jbase.core.kv.KVStore;
import com.jbase.core.storage.FileStore;
import com.jbase.core.storage.MetaPage;
import com.jbase.core.storage.Page;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {

        Path path = Path.of("jbase.db");
        FileStore store = new FileStore(path);

        if (store.isEmpty()) {
            Page meta = store.allocate(); 
            MetaPage mp = new MetaPage(meta.getData());
            mp.setRootPageId(-1);
        }

        BTree tree = new BTree(store);
        KVStore db = new KVStore(tree);

        if (args.length > 0 && args[0].equals("write")) {
            db.put("a".getBytes(), "1".getBytes());
            db.put("b".getBytes(), "2".getBytes());
            db.put("c".getBytes(), "3".getBytes());
            System.out.println("WRITE DONE");
        } else {
            System.out.println(new String(db.get("a".getBytes())));
            System.out.println(new String(db.get("b".getBytes())));
            System.out.println(new String(db.get("c".getBytes())));
        }
    }
}
