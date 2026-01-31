## BTreeNode.java

ref → src/main/java/com/jbase/core/index/BTreeNode.java

---

### Purpose

Represents a **single node** inside the B+ tree.
This class is a **pure data structure** — it contains no logic, only state.

Each `BTreeNode` is serialized to / deserialized from a disk page.

---

### Design Principles

* Same structure used for **leaf nodes** and **internal nodes**
* Behavior depends on `isLeaf`
* Stores **page IDs**, not object references (disk-first design)
* Optimized for persistence and range scans

---

### Field Breakdown

| **Field**  | **Logic**                                               |
| ---------- | ------------------------------------------------------- |
| `isLeaf`   | Determines whether this node is a leaf or internal node |
| `keys`     | Sorted list of keys used for binary search              |
| `values`   | Values associated with keys (only valid for leaf nodes) |
| `children` | Child page IDs (only valid for internal nodes)          |
| `nextLeaf` | Page ID of the next leaf node for fast range scans      |

---

### Internal Node Layout

Internal nodes **do not store values**.
They only route searches using keys and child pointers.

```
keys:     [10 | 20 | 30]
children: [ 3 | 7 | 9 | 12 ]   // page IDs
```

Rule:

* `children.size() == keys.size() + 1`

---

### Leaf Node Layout

Leaf nodes store actual data and are linked together.

```
keys:     [10 | 12 | 15]
values:   [ A |  B |  C ]
nextLeaf: → page 14
```

Properties:

* Keys and values align by index
* `nextLeaf` enables ordered scans without tree traversal

---

### Why `nextLeaf` Matters

* Allows `scan()` to run in **O(n)** instead of **O(n log n)**
* Matches how production databases implement range queries
* Core feature of B+ trees (not plain B-trees)

