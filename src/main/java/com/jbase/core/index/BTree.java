package com.jbase.core.index;

import com.jbase.core.storage.MemStore;
import com.jbase.core.storage.Page;

import java.util.Arrays;

public class BTree {

    private final MemStore store;
    private int rootPageId;

    public BTree(MemStore store) {
        this.store = store;

        Page root = store.allocate();
        BTreeNode rootNode = new BTreeNode();
        rootNode.isLeaf = true;

        rootPageId = root.getId();
        NodeCache.put(rootPageId, rootNode);
    }

    public byte[] search(byte[] key) {
        return searchRecursive(rootPageId, key);
    }

    private byte[] searchRecursive(int pageId, byte[] key) {
        BTreeNode node = NodeCache.get(pageId);

        int idx = findKeyIndex(node.keys, key);

        if (node.isLeaf) {
            if (idx < node.keys.size() &&
                    Arrays.compare(node.keys.get(idx), key) == 0) {
                return node.values.get(idx);
            }
            return null;
        } else {
            return searchRecursive(node.children.get(idx), key);
        }
    }

    private int findKeyIndex(java.util.List<byte[]> keys, byte[] key) {
        int low = 0, high = keys.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = Arrays.compare(keys.get(mid), key);
            if (cmp < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return low;
    }
}
