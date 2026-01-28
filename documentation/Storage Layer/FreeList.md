// ref -> src/main/java/com/jbase/core/storage/FreeList.java

**Code** | **Logic**
---------|----------
`Deque<Integer>` | Stores page IDs that are no longer in use.
`release(pageId)` | Called when a page becomes free.
`acquire()` | Gives back a reusable page ID, or null if none available.

```
FreeList
┌──────────────┐
│ 7            │ ← reusable page IDs
│ 3            │
│ 1            │
└──────────────┘
```

So this basically works like :

When you need a page:
* Ask FreeList first ( reuse if possible )
* If empty -> Memstore allocates a new page


