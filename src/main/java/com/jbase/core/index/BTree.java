package com.jbase.core.index;

import com.jbase.core.storage.MemStore;
import com.jbase.core.storage.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BTree {

    static final int MAX_KEYS = 4;

    static class SplitResult {
        byte[] promotedKey;
        int rightPageId;

        SplitResult(byte[] key, int pageId) {
            this.promotedKey = key;
            this.rightPageId = pageId;
        }
    }

    private final MemStore store;
    private int rootPageId;

    public BTree(MemStore store) {
        this.store = store;

        Page root = store.allocate();
        BTreeNode rootNode = new BTreeNode();
        rootNode.isLeaf = true;

        rootPageId = root.getId();
        writeNode(rootPageId, rootNode);
    }

    public byte[] search(byte[] key) {
        return searchRecursive(rootPageId, key);
    }

    private byte[] searchRecursive(int pageId, byte[] key) {
        BTreeNode node = readNode(pageId);
        int idx = findKeyIndex(node.keys, key);

        if (node.isLeaf) {
            if (idx < node.keys.size()
                    && Arrays.compare(node.keys.get(idx), key) == 0) {
                return node.values.get(idx);
            }
            return null;
        }

        int childIdx = idx;
        if (idx < node.keys.size()
                && Arrays.compare(key, node.keys.get(idx)) >= 0) {
            childIdx = idx + 1;
        }
        return searchRecursive(node.children.get(childIdx), key);
    }

    public void insert(byte[] key, byte[] value) {
        SplitResult res = insertRecursive(rootPageId, key, value);

        if (res != null) {
            BTreeNode newRoot = new BTreeNode();
            newRoot.isLeaf = false;

            newRoot.keys.add(res.promotedKey);
            newRoot.children.add(rootPageId);
            newRoot.children.add(res.rightPageId);

            Page rootPage = store.allocate();
            int newRootPageId = rootPage.getId();
            writeNode(newRootPageId, newRoot);

            rootPageId = newRootPageId;
        }
    }

    private SplitResult insertRecursive(int pageId, byte[] key, byte[] value) {
        BTreeNode node = readNode(pageId);
        int idx = findKeyIndex(node.keys, key);

        if (node.isLeaf) {
            return insertIntoLeaf(node, pageId, idx, key, value);
        } else {
            return insertIntoInternal(node, pageId, idx, key, value);
        }
    }

    private SplitResult insertIntoLeaf(
            BTreeNode node,
            int pageId,
            int idx,
            byte[] key,
            byte[] value) {

        if (idx < node.keys.size()
                && Arrays.compare(node.keys.get(idx), key) == 0) {
            node.values.set(idx, value);
            writeNode(pageId, node);
            return null;
        }

        node.keys.add(idx, key);
        node.values.add(idx, value);

        if (node.keys.size() <= MAX_KEYS) {
            writeNode(pageId, node);
            return null;
        }

        int mid = node.keys.size() / 2;

        BTreeNode right = new BTreeNode();
        right.isLeaf = true;

        right.keys.addAll(node.keys.subList(mid, node.keys.size()));
        right.values.addAll(node.values.subList(mid, node.values.size()));

        node.keys.subList(mid, node.keys.size()).clear();
        node.values.subList(mid, node.values.size()).clear();

        right.nextLeaf = node.nextLeaf;

        Page rightPage = store.allocate();
        int rightPageId = rightPage.getId();
        writeNode(rightPageId, right);

        node.nextLeaf = rightPageId;
        writeNode(pageId, node);

        return new SplitResult(right.keys.get(0), rightPageId);
    }

    private SplitResult insertIntoInternal(
            BTreeNode node,
            int pageId,
            int idx,
            byte[] key,
            byte[] value) {

        int childIdx = idx;
        if (idx < node.keys.size()
                && Arrays.compare(key, node.keys.get(idx)) >= 0) {
            childIdx = idx + 1;
        }

        int childPageId = node.children.get(childIdx);
        SplitResult res = insertRecursive(childPageId, key, value);

        if (res == null) {
            return null;
        }

        node.keys.add(idx, res.promotedKey);
        node.children.add(idx + 1, res.rightPageId);

        if (node.keys.size() <= MAX_KEYS) {
            writeNode(pageId, node);
            return null;
        }

        int mid = node.keys.size() / 2;
        byte[] promote = node.keys.get(mid);

        BTreeNode right = new BTreeNode();
        right.isLeaf = false;

        right.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
        right.children.addAll(node.children.subList(mid + 1, node.children.size()));

        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();

        Page rightPage = store.allocate();
        int rightPageId = rightPage.getId();
        writeNode(rightPageId, right);

        writeNode(pageId, node);

        return new SplitResult(promote, rightPageId);
    }

    public List<byte[]> scan() {
        List<byte[]> result = new ArrayList<>();

        int pageId = rootPageId;
        BTreeNode node = readNode(pageId);

        while (!node.isLeaf) {
            pageId = node.children.get(0);
            node = readNode(pageId);
        }

        while (true) {
            result.addAll(node.keys);
            if (node.nextLeaf == -1)
                break;
            node = readNode(node.nextLeaf);
        }

        return result;
    }

    private BTreeNode readNode(int pageId) {
        return NodeSerializer.deserialize(store.get(pageId).getData());
    }

    private void writeNode(int pageId, BTreeNode node) {
        byte[] data = NodeSerializer.serialize(node);
        System.arraycopy(data, 0, store.get(pageId).getData(), 0, data.length);
    }

    private int findKeyIndex(List<byte[]> keys, byte[] key) {
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
