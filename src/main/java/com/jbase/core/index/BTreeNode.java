package com.jbase.core.index;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode {

    public boolean isLeaf;

    // keys are always sorted which are used for binary search
    public List<byte[]> keys = new ArrayList<>();

    // only store leaf nodes ( because only leaf nodes store values)
    public List<byte[]> values = new ArrayList<>();

    // for internal nodes that has child page IDs
    public List<Integer> children = new ArrayList<>();

    public int nextLeaf = -1;

}
