## PageStore.java

ref → src/main/java/com/jbase/core/storage/PageStore.java

---

### Purpose

`PageStore` defines the **storage abstraction layer** for JBase.

It represents a pluggable backend that manages how pages are:

* allocated
* read
* written
* reclaimed

Both **in-memory** and **disk-backed** implementations conform to this interface.

---

### Why This Interface Exists

Database layers should not care *where* data lives.

By abstracting storage behind `PageStore`:

* the B+ tree stays storage-agnostic
* memory and disk stores share the same logic
* persistence can be added without rewriting the index

This mirrors real database designs (e.g., buffer managers).

---

### Core Responsibilities

```
BTree / KVStore
        ↓
     PageStore
        ↓
  (FileStore / MemStore)
```

---

### Method Breakdown

| **Method**     | **Logic**                                                |
| -------------- | -------------------------------------------------------- |
| `allocate()`   | Creates a new empty page and assigns it a unique page ID |
| `get(pageId)`  | Loads an existing page by ID                             |
| `write(page)`  | Persists page contents to the underlying store           |
| `free(pageId)` | Marks a page as reusable (free list support)             |

---

### Design Notes

* `allocate()` does **not** reuse pages directly
  Reuse is handled by the free list
* `get()` always returns a `Page` object with data populated
* `write()` ensures page modifications are persisted
* `free()` is currently a hook for future space reclamation

---

### Storage Implementations

| Implementation | Backend                |
| -------------- | ---------------------- |
| `MemStore`     | Heap memory (volatile) |
| `FileStore`    | Disk (persistent)      |

Both implement the same interface without changing B+ tree logic.

---

### Why This Matters

This abstraction allows:

* swapping storage engines transparently
* testing the index in memory
* persisting data to disk with zero tree changes

