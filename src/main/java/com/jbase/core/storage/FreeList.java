package com.jbase.core.storage;

import java.util.ArrayDeque;
import java.util.Deque;

public class FreeList {

    private final Deque<Integer> freePage = new ArrayDeque<>();

    public void release(int pageId) {
        freePage.push(pageId);
    }

    public Integer acquire() {
        return freePage.poll();
    }

}
