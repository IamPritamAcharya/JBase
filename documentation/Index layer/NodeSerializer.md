## NodeSerializer.java

ref → src/main/java/com/jbase/core/index/NodeSerializer.java

### Purpose

Responsible for **converting `BTreeNode` objects to raw bytes and back** so they can be safely stored inside fixed-size pages on disk.
This class defines the **on-disk binary layout** of every B+ tree node.

---

### Why This Exists

* Pages store only `byte[]`
* Tree nodes are Java objects
* Serialization bridges the gap
* Ensures disk persistence + crash safety

---

### Binary Layout (On Disk)

| **Order** | **Field**            | **Reason**                              |
| --------- | -------------------- | --------------------------------------- |
| 1         | `MAGIC` (int)        | Detect corruption / uninitialized pages |
| 2         | `isLeaf` (byte)      | Distinguish leaf vs internal node       |
| 3         | `keyCount` (int)     | Number of keys stored                   |
| 4         | `nextLeaf` (int)     | Leaf chaining for range scans           |
| 5         | `keys`               | Variable-length key data                |
| 6         | `values OR children` | Depends on node type                    |

---

### MAGIC Header

| **Field**            | **Logic**                   |
| -------------------- | --------------------------- |
| `MAGIC = 0x4E4F4445` | ASCII `"NODE"` marker       |
| Written first        | Allows corruption detection |
| Checked on read      | Prevents undefined behavior |

If the magic value does not match, deserialization fails immediately.

---

### Serialization Logic (`serialize`)

| **Step**           | **Logic**                            |
| ------------------ | ------------------------------------ |
| Allocate buffer    | Fixed page size (4096 bytes)         |
| Write MAGIC        | Marks page as initialized            |
| Write node type    | Leaf or internal                     |
| Write key metadata | Key count + nextLeaf                 |
| Write keys         | `[length][bytes]` format             |
| Write payload      | Values (leaf) or children (internal) |

**Leaf Node Payload**

* Each value stored as:

  * `-1` → logical delete
  * `[length][bytes]` → actual value

**Internal Node Payload**

* Stores `keys + 1` child page IDs
* Children are integers, not object references

---

### Deserialization Logic (`deserialize`)

| **Step**          | **Logic**                        |
| ----------------- | -------------------------------- |
| Read MAGIC        | Validate page integrity          |
| Read node type    | Leaf or internal                 |
| Read key metadata | Key count + nextLeaf             |
| Rebuild keys      | Exact order preserved            |
| Rebuild payload   | Values or children based on type |

If the page is uninitialized or corrupt, an exception is thrown early.

---

### Structural Guarantees

* Keys are read in the same order they were written
* Internal nodes always reconstruct `keys + 1` children
* Leaf nodes always reconstruct `values.size() == keys.size()`
* No object references are persisted — only raw data

---

### Why Fixed-Size Buffer (4096 bytes)

* Matches `Page.PAGE_SIZE`
* Simplifies disk I/O
* Prevents partial writes
* Enables predictable offsets

Unused space remains zero-filled.

---

### Failure Safety

* MAGIC header prevents reading garbage pages
* Early validation avoids silent corruption
* Logical deletes (`null` values) are preserved correctly

