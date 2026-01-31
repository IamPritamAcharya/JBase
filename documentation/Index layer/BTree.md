## BTree.java

ref → src/main/java/com/jbase/core/index/BTree.java

### Purpose

Implements a **disk-backed B+ Tree** on top of `PageStore`.
This is the core indexing structure used by the KV layer to store and retrieve key–value pairs efficiently.

---

### High-Level Concepts

| **Concept**       | **Logic**                                                  |
| ----------------- | ---------------------------------------------------------- |
| B+ Tree           | Balanced tree optimized for disk I/O                       |
| Page-based        | Each node lives inside a fixed-size page                   |
| Root persistence  | Root page ID is stored in `MetaPage`                       |
| Split propagation | Node splits bubble upward recursively                      |
| Leaf chaining     | Leaf nodes are linked for fast range scans                 |
| Page IDs          | Children are referenced by page IDs, not object references |

---

### Tree Initialization

| **Step**         | **Logic**                                |
| ---------------- | ---------------------------------------- |
| Read MetaPage    | Page `0` is reserved for metadata        |
| Check rootPageId | `-1` or `0` means tree is uninitialized  |
| Allocate root    | First real node is always page ≥ 1       |
| Persist root     | Root page ID is written back to MetaPage |

This guarantees the root node never overlaps with the metadata page.

---

### Public API

| **Method** | **Logic**                                 |
| ---------- | ----------------------------------------- |
| `insert`   | Insert or overwrite a key–value pair      |
| `search`   | Lookup a key via recursive tree descent   |
| `scan`     | In-order traversal using leaf-level links |

---

### Search Algorithm

**Flow**

1. Read the node from disk
2. Binary search keys
3. If leaf → return value
4. Else → descend into correct child

**Safety Check**

* Detects self-referencing child pointers
* Throws immediately on structural corruption

---

### Insert Algorithm

**Flow**

1. Descend recursively to a leaf
2. Insert key/value in sorted order
3. If node overflows → split
4. Promote middle key upward
5. Repeat until root (new root created if required)

---

### Key Internal Methods

| **Method**           | **Logic**                           |
| -------------------- | ----------------------------------- |
| `insertRecursive`    | Core recursive insert logic         |
| `insertIntoLeaf`     | Insert into leaf, split on overflow |
| `insertIntoInternal` | Handle child split & key promotion  |
| `findKeyIndex`       | Binary search helper                |
| `readNode`           | Deserialize a node from a page      |
| `writeNode`          | Serialize node and persist to disk  |

---

### Leaf Node Split

Before:
keys = `[10, 20, 30, 40, 50]`

After:
left = `[10, 20]`
right = `[30, 40, 50]`

Promoted key → `30`

* Right node becomes a new sibling
* `nextLeaf` pointers are updated
* Promoted key is inserted into the parent

---

### Internal Node Split

Before:
keys = `[10, 20, 30, 40, 50]`
children = `[A, B, C, D, E, F]`

After:
left

* keys = `[10, 20]`
* children = `[A, B, C]`

right

* keys = `[40, 50]`
* children = `[D, E, F]`

Promoted key → `30`

---

### Structural Invariants

* `children.size() == keys.size() + 1` for internal nodes
* Leaf nodes never have children
* Root page ID is never `0`
* Keys remain sorted at all times
* Tree height grows upward only

---

### Notes

* Page `0` is strictly reserved for metadata
* The tree is fully persistent across restarts
* Designed for correctness before optimization
* Forms the foundation of the JBase storage engine
