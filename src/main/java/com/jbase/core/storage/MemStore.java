package com.jbase.core.storage;

import java.util.HashMap;
import java.util.Map;

public class MemStore implements PageStore {

    private int nextPageId = 0;
    private final Map<Integer, Page> pages = new HashMap<>();
    private final FreeList freeList = new FreeList(this);

    public Page allocate() {
        Integer reused = freeList.acquire();
        if (reused != null) {
            return get(reused);
        }
        Page page = new Page(nextPageId++);
        pages.put(page.getId(), page);
        return page;
    }

    public Page get(int pageId) {
        return pages.get(pageId);
    }

    public void free(int pageId) {
        freeList.release(pageId);
    }

    @Override
    public void write(Page page) {
        // TODO Auto-generated method stub
        
    }

}
