package com.jbase.core.storage;

public class Page {
    public static final int PAGE_SIZE = 4096;

    private final int id;
    private final byte[] data;

    public Page(int id) {
        this.id = id;
        this.data = new byte[PAGE_SIZE];
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
