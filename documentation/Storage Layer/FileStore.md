## FileStore.java

ref → src/main/java/com/jbase/core/storage/FileStore.java

---

### Purpose

`FileStore` is the **disk-backed page manager** of JBase.
It maps fixed-size pages directly onto a file using random access I/O.

This class is responsible for **persistence**, **page allocation**, and **page reads/writes**.

---

### Design Principles

* Pages are **fixed-size blocks** (`Page.PAGE_SIZE`)
* Page ID = file offset / page size
* Uses `RandomAccessFile` for precise byte-level control
* Thread-safe via `synchronized` methods
* No caching — always reads/writes directly to disk

---

### Field Breakdown

| **Field**    | **Logic**                              |
| ------------ | -------------------------------------- |
| `PAGE_SIZE`  | Size of each page (shared with `Page`) |
| `file`       | Backing storage file                   |
| `nextPageId` | Tracks the next free page ID           |

---

### Constructor Logic

* Opens (or creates) the database file in `rw` mode
* Computes `nextPageId` from file size
* Enables **crash-safe persistence** across restarts

```
nextPageId = file.length() / PAGE_SIZE
```

---

### Method Breakdown

| **Method**     | **Logic**                                       |
| -------------- | ----------------------------------------------- |
| `allocate()`   | Creates a new empty page and appends it to disk |
| `get(pageId)`  | Reads a page from disk into memory              |
| `write(page)`  | Writes a page’s bytes to its fixed offset       |
| `read(page)`   | Loads page data from disk                       |
| `free(pageId)` | Placeholder for future space reuse              |
| `isEmpty()`    | Checks whether the file contains any pages      |
| `close()`      | Closes the underlying file safely               |

---

### Page Addressing Model

```
file offset = pageId * PAGE_SIZE
```

This guarantees:

* O(1) page access
* Deterministic disk layout
* Easy recovery and debugging

---

### Why `synchronized`

* Prevents concurrent writes corrupting the file
* Ensures page allocation is atomic
* Simplifies correctness in early design stages

---

### Why `free()` Is Empty

* Page reuse is a **future optimization**
* Current model favors correctness over complexity
* Matches how many databases start (append-only)

---

### Failure Handling

* All I/O failures are wrapped as unchecked exceptions
* Prevents silent data corruption
* Forces crashes instead of inconsistent state

---

### Summary

`FileStore` is the **storage engine backbone** of JBase.
It turns a flat file into a page-addressable disk structure that higher layers (BTree, KVStore) rely on.

