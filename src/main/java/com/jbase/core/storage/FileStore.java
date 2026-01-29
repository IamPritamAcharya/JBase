package com.jbase.core.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class FileStore {

    private static final int PAGE_SIZE = Page.PAGE_SIZE;

    private final RandomAccessFile file;
    private int nextPageId = 0;

    public FileStore(Path path) throws IOException {
        this.file = new RandomAccessFile(path.toFile(), "rw");
        this.nextPageId = (int) (file.length() / PAGE_SIZE);
    }

    public synchronized Page allocate() throws IOException {
        int pageId = nextPageId++;
        Page page = new Page(pageId);
        write(page);
        return page;
    }

    public synchronized Page get(int pageId) throws IOException {
        Page page = new Page(pageId);
        read(page);
        return page;
    }

    public synchronized void write(Page page) throws IOException {
        file.seek((long) page.getId() * PAGE_SIZE);
        file.write(page.getData());
    }

    public synchronized void read(Page page) throws IOException {
        file.seek((long) page.getId() * PAGE_SIZE);
        file.readFully(page.getData());
    }

    public void close() throws IOException {
        file.close();
    }

}
