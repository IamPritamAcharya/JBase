package com.jbase.core.index;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class NodeSerializer {
    public static byte[] serialize(BTreeNode node) {
        ByteBuffer buf = ByteBuffer.allocate(4096);

        buf.put((byte) (node.isLeaf ? 1 : 0));
        buf.putInt(node.keys.size());
        buf.putInt(node.nextLeaf);

        for (byte[] key : node.keys) {
            buf.putInt(key.length);
            buf.put(key);
        }

        if (node.isLeaf) {
            for (byte[] val : node.values) {
                if (val == null) {
                    buf.putInt(-1);
                } else {
                    buf.putInt(val.length);
                    buf.put(val);
                }
            }
        } else {
            int expectedChildren = node.keys.size() + 1;

            for (int i = 0; i < expectedChildren; i++) {
                buf.putInt(node.children.get(i));
            }

        }

        return buf.array();
    }

    public static BTreeNode deserialize(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);

        BTreeNode node = new BTreeNode();

        node.isLeaf = buf.get() == 1;
        int keyCount = buf.getInt();
        node.nextLeaf = buf.getInt();

        node.keys = new ArrayList<>();
        for (int i = 0; i < keyCount; i++) {
            int len = buf.getInt();
            byte[] key = new byte[len];
            buf.get(key);
            node.keys.add(key);
        }

        if (node.isLeaf) {
            node.values = new ArrayList<>();
            for (int i = 0; i < keyCount; i++) {
                int len = buf.getInt();
                if (len == -1) {
                    node.values.add(null);
                } else {
                    byte[] val = new byte[len];
                    buf.get(val);
                    node.values.add(val);
                }
            }
        } else {
            node.children = new ArrayList<>();
            for (int i = 0; i < keyCount + 1; i++) {
                node.children.add(buf.getInt());
            }
        }

        return node;
    }
}
