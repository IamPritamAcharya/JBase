## Page.java

ref → src/main/java/com/jbase/core/storage/Page.java

---

### Purpose

`Page` represents the **fundamental storage unit** of JBase.

Every structure in the database—B+ tree nodes, free list pages, metadata—is stored and retrieved in **fixed-size pages**.

This mirrors how real databases and filesystems operate.

---

### Why Pages Exist

Databases do not work with variable-length memory blocks.
They operate on fixed-size pages to achieve:

* predictable I/O
* alignment with disk blocks
* efficient caching
* easy addressing

JBase uses **4 KB pages**, which is a standard industry size.

---

### Page Layout

```
| Page ID | 4 KB Raw Data |
```

The `Page` object itself does **not** interpret the contents.
It simply holds raw bytes.

Higher layers decide how to encode and decode the data.

---

### Field Breakdown

| **Field**   | **Logic**                                   |
| ----------- | ------------------------------------------- |
| `PAGE_SIZE` | Fixed size of every page (4096 bytes)       |
| `id`        | Logical page identifier used for addressing |
| `data`      | Raw byte buffer storing page contents       |

---

### Method Breakdown

#### `Page(int id)`

Creates a new page with:

* a unique page ID
* a zero-initialized 4 KB byte buffer

---

#### `getId()`

Returns the logical page ID.

Used by:

* page stores
* index structures
* child pointers in B+ tree nodes

---

#### `getData()`

Returns the raw byte array backing the page.

Used by:

* serializers (`NodeSerializer`)
* metadata handlers (`MetaPage`)
* free list pages

---

### Design Characteristics

* No logic
* No serialization
* No disk access
* Pure data container

This strict separation keeps the system clean and composable.

---

### Why This Matters

By isolating raw storage into a `Page` abstraction:

* The storage engine can evolve independently
* Different page layouts can coexist
* Disk-backed and memory-backed stores share the same API

