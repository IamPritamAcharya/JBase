## FreeList.java

ref → src/main/java/com/jbase/core/storage/FreeList.java

---

### Purpose

`FreeList` implements an **in-memory free page manager**.
It tracks page IDs that have been released and can be reused later, avoiding unnecessary allocations.

This is an early optimization layer used by `MemStore`.

---

### Design Principles

* Stack-based free list (LIFO)
* Stored **inside pages themselves**
* Grows dynamically as more pages are freed
* Simple and fast, no disk I/O involved

---

### Field Breakdown

| **Field**    | **Logic**                                               |
| ------------ | ------------------------------------------------------- |
| `store`      | Reference to the in-memory page store                   |
| `headPageId` | Page ID of the current freelist head (`-1` means empty) |

---

### Internal Structure

The free list is stored as a **linked list of FreeListPages**.

Each `FreeListPage` contains:

* A fixed-size array of free page IDs
* A `count` of used slots
* A `next` pointer to the previous freelist page

```
headPage
 ├─ [12, 9, 5, ...]
 ├─ count = 3
 └─ next → previous freelist page
```

---

### Method Breakdown

#### `release(int pageId)`

Adds a page ID back into the free list.

**Steps**

1. If freelist is empty, allocate the first freelist page
2. Load the current head freelist page
3. If full, allocate a new freelist page and link it
4. Append the released page ID
5. Increment the count

**Why**

* Keeps allocation O(1)
* Avoids scanning or compaction

---

#### `acquire()`

Returns a reusable page ID if available.

**Steps**

1. If freelist is empty → return `null`
2. Read the head freelist page
3. If empty, move to next freelist page recursively
4. Pop the last page ID (stack behavior)
5. Decrement the count

**Returns**

* `Integer pageId` if reusable
* `null` if no pages are free

---

### Key Properties

* LIFO reuse improves cache locality
* Recursive acquire handles empty freelist pages cleanly
* No memory fragmentation issues
* Extremely simple and fast

---

### Limitations

* In-memory only (not persisted)
* No concurrency control
* No page coalescing
* Currently unused by `FileStore`

