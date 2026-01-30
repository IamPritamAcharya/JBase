package com.jbase.core.index;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode {
    public boolean isLeaf;
    public List<byte[]> keys = new ArrayList<>();
    public List<byte[]> values = new ArrayList<>();
    public List<Integer> children = new ArrayList<>();
    public int nextLeaf = -1;
}
