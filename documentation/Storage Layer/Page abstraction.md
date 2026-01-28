// ref -> src/main/java/com/jbase/core/storage/Page.java

**Code** | **Logic**
---------|----------
`PAGE_SIZE = 4096` | Database don't work with objects, they work with fixed size blocks (pages). 4 KB is the industry default.
`id` | reference to page Id
`byte[] data` | This simulates raw memory/disk bytes. B+ tree nodes will live inside this array

```
┌───────────────┐
│ Page ID = 12  │
│───────────────│
│ 4096 bytes    │  ← B+ tree nodes will be serialized here
│ (raw memory)  │
│               │
└───────────────┘
```

This exists purely for storing.