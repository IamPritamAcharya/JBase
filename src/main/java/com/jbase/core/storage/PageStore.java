package com.jbase.core.storage;

public interface PageStore {
    Page allocate();

    Page get(int pageId);

    void free(int pageId);

    void write(Page page);

}
