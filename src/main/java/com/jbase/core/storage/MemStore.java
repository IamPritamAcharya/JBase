package com.jbase.core.storage;

import java.util.HashMap;
import java.util.Map;

public class MemStore {

    private int nextPageId = 0;
    private final Map<Integer, Page> pages = new HashMap<>();

    public Page allocate() {
        Page page = new Page(nextPageId++);
        pages.put(page.getId(), page);
        return page;
    }

    public Page get(int pageId) {
        return pages.get(pageId);
    }

}
