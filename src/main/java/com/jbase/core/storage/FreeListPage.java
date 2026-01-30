package com.jbase.core.storage;

import java.nio.ByteBuffer;

public class FreeListPage {

    private static final int HEADER_SIZE = 8;
    private static final int ENTRY_SIZE = 4;

    private final ByteBuffer buf;

    public FreeListPage(byte[] data) {
        this.buf = ByteBuffer.wrap(data);
    }

    public int getNext() {
        return buf.getInt(0);
    }

    public void setNext(int pageId) {
        buf.putInt(0, pageId);
    }

    public int getCount() {
        return buf.getInt(4);
    }

    public void setCount(int count) {
        buf.putInt(4, count);
    }

    public int get(int idx) {
        return buf.getInt(HEADER_SIZE + idx * ENTRY_SIZE);
    }

    public void set(int idx, int pageId) {
        buf.putInt(HEADER_SIZE + idx * ENTRY_SIZE, pageId);
    }

    public int capacity() {
        return (Page.PAGE_SIZE - HEADER_SIZE) / ENTRY_SIZE;
    }
}
