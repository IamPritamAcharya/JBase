package com.jbase.core.storage;

public class FreeList {

    private final MemStore store;
    private int headPageId = -1;

    public FreeList(MemStore store) {
        this.store = store;
    }

    public void release(int pageId) {
        if (headPageId == -1) {
            Page page = store.allocate();
            headPageId = page.getId();
            FreeListPage fl = new FreeListPage(page.getData());
            fl.setNext(-1);
            fl.setCount(0);
        }

        Page head = store.get(headPageId);
        FreeListPage fl = new FreeListPage(head.getData());

        if (fl.getCount() == fl.capacity()) {
            Page newHead = store.allocate();
            FreeListPage newFl = new FreeListPage(newHead.getData());
            newFl.setNext(headPageId);
            newFl.setCount(0);
            headPageId = newHead.getId();
            fl = newFl;
            head = newHead;
        }

        fl.set(fl.getCount(), pageId);
        fl.setCount(fl.getCount() + 1);
    }

    public Integer acquire() {
        if (headPageId == -1)
            return null;

        Page head = store.get(headPageId);
        FreeListPage fl = new FreeListPage(head.getData());

        int count = fl.getCount();
        if (count == 0) {
            headPageId = fl.getNext();
            return acquire();
        }

        int pageId = fl.get(count - 1);
        fl.setCount(count - 1);
        return pageId;
    }
}
