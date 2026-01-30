package com.jbase.core.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class FileStore implements PageStore {

    private static final int PAGE_SIZE = Page.PAGE_SIZE;

    private final RandomAccessFile file;
    private int nextPageId = 0;

    public FileStore(Path path) throws IOException {
        this.file = new RandomAccessFile(path.toFile(), "rw");
        this.nextPageId = (int) (file.length() / PAGE_SIZE);
    }

    @Override
    public synchronized Page allocate() {
        try {
            int pageId = nextPageId++;
            Page page = new Page(pageId);
            write(page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Page get(int pageId) {
        try {
            Page page = new Page(pageId);
            read(page);
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void write(Page page) {
        try {
            file.seek((long) page.getId() * PAGE_SIZE);
            file.write(page.getData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void read(Page page) throws IOException {
        file.seek((long) page.getId() * PAGE_SIZE);
        file.readFully(page.getData());
    }

    public void close() throws IOException {
        file.close();
    }

    @Override
    public void free(int pageId) {
    }

    public boolean isEmpty() {
        return nextPageId == 0;
    }
}
