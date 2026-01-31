## MetaPage.java

ref → src/main/java/com/jbase/core/storage/MetaPage.java

---

### Purpose

`MetaPage` represents the **database metadata page**.
It stores global information required to bootstrap and navigate the database, most importantly the **root page ID of the B+ Tree**.

This page is always stored at **page 0** and is read before any other structure.

---

### Why a Meta Page Exists

Every database needs a well-known entry point.
`MetaPage` solves this by acting as the **single source of truth** for:

* Where the index starts
* How the database should be initialized on restart

Without this, persistence across restarts would not be possible.

---

### Page Layout

```
| Offset | Size | Field        |
|--------|------|--------------|
| 0      | 4    | rootPageId  |
| 4..    | ...  | (reserved)  |
```

Only the first 4 bytes are currently used.
The rest of the page is intentionally left unused for future metadata.

---

### Field Breakdown

| **Field**    | **Logic**                             |
| ------------ | ------------------------------------- |
| `buf`        | ByteBuffer wrapper over raw page data |
| `rootPageId` | Page ID of the B+ tree root node      |

---

### Method Breakdown

#### `getRootPageId()`

Reads the root page ID from offset `0`.

Used during startup to determine whether:

* the tree already exists, or
* a new tree must be created

---

#### `setRootPageId(int pageId)`

Writes the root page ID at offset `0`.

Called when:

* the tree is created for the first time
* the root splits and a new root is allocated

---

### Design Characteristics

* Fixed offset metadata
* Zero allocations
* No object serialization
* Direct memory manipulation

---

### Why This Design Is Important

This mirrors real database engines:

* PostgreSQL → catalog + control files
* SQLite → header page
* RocksDB → manifest

By keeping metadata isolated, the storage layer remains:

* restart-safe
* crash-recoverable (future work)
* extensible

---

### Summary

`MetaPage` is the **bootstrap anchor** of JBase.
It ensures the database can always find its index root and safely resume operation after restart.

Send the next file when ready.
