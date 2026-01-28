package com.jbase.core.index;

import java.util.HashMap;
import java.util.Map;

public class NodeCache {
    private static final Map<Integer, BTreeNode> nodes = new HashMap<>();

    public static void put(int pageId, BTreeNode node) {
        nodes.put(pageId, node);
    }

    public static BTreeNode get(int pageId) {
        return nodes.get(pageId);
    }
}
