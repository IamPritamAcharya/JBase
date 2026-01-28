// ref -> src/main/java/com/jbase/core/storage/MemStore.java

**Code** | **Logic**
---------|----------
`nextPageId` | Simulates disk offsets / page numbering. Every page gets a unique ID.
`Map<Integer, Page> pages` | Acts like RAM backed disk : pageId -> page bytes
`allocate` | Equivalent to "give me a new empty page". DBs never new nodes directly.
`get(pageId)` | Higher layers only talk in page IDs, never object references

```
MemStore
┌──────────────────────────┐
│ pageId → Page            │
│ 0 → [4096 bytes]         │
│ 1 → [4096 bytes]         │
│ 2 → [4096 bytes]         │
└──────────────────────────┘
```

This is a fake disk now.

It only knows 'I allocate pages and give them back'.