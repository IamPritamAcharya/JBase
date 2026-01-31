## FreeListPage.java

ref → src/main/java/com/jbase/core/storage/FreeListPage.java

---

### Purpose

`FreeListPage` defines the **on-page binary layout** for a freelist node.
It wraps a raw page (`byte[]`) and provides structured access to metadata and stored free page IDs.

This is a **low-level page format**, similar to how real databases encode internal structures.

---

### Page Layout

```
| Offset | Size | Field        |
|--------|------|--------------|
| 0      | 4    | nextPageId   |
| 4      | 4    | count        |
| 8      | 4    | entry[0]     |
| 12     | 4    | entry[1]     |
| ...    | ...  | ...          |
```

---

### Field Breakdown

| **Field**     | **Logic**                                    |
| ------------- | -------------------------------------------- |
| `HEADER_SIZE` | Bytes reserved for metadata (`next + count`) |
| `ENTRY_SIZE`  | Size of each stored page ID (int = 4 bytes)  |
| `buf`         | ByteBuffer view over the raw page data       |

---

### Method Breakdown

#### `getNext()`

Returns the page ID of the **next freelist page**.

Used to form a linked list of freelist pages.

---

#### `setNext(int pageId)`

Sets the pointer to the next freelist page.

---

#### `getCount()`

Returns how many free page IDs are currently stored in this page.

---

#### `setCount(int count)`

Updates the number of valid entries.

---

#### `get(int idx)`

Returns the page ID stored at index `idx`.

Computed offset:

```
HEADER_SIZE + idx * ENTRY_SIZE
```

---

#### `set(int idx, int pageId)`

Stores a page ID at index `idx`.

---

#### `capacity()`

Returns the **maximum number of page IDs** this freelist page can store.

```
(Page.PAGE_SIZE - HEADER_SIZE) / ENTRY_SIZE
```

This ensures the freelist page fully utilizes available space.

---

### Design Characteristics

* Fixed-size binary layout
* No object allocation per entry
* Fast direct memory access
* Matches real database internals (Postgres / SQLite style)

---

### Why This Matters

Separating:

* **FreeList** → logic
* **FreeListPage** → binary layout

keeps the system:

* modular
* debuggable
* extensible for disk persistence later
