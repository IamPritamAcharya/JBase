## KVStore.java

ref → src/main/java/com/jbase/core/kv/KVStore.java

### Purpose

Provides a **simple key–value database interface** on top of the B+ tree index.
This is the layer that application code interacts with directly.

KVStore intentionally hides all tree, page, and storage details.

---

### Design Role

* Acts as the **public API** of the database
* Delegates all storage and indexing logic to `BTree`
* Keeps the system modular:

  * Index logic lives in `core.index`
  * Storage logic lives in `core.storage`
  * KVStore wires them together

---

### Fields

| **Field** | **Logic**                                            |
| --------- | ---------------------------------------------------- |
| `tree`    | The underlying B+ tree used for indexing and storage |

---

### Method Breakdown

| **Method** | **Logic**                                  |
| ---------- | ------------------------------------------ |
| `put`      | Insert or overwrite a key–value pair       |
| `get`      | Lookup a value using the B+ tree           |
| `delete`   | Logical delete by inserting a `null` value |
| `scan`     | Range scan over all keys in sorted order   |

---

### Method Details

#### `put(byte[] key, byte[] value)`

* Inserts a new key or overwrites an existing one
* Delegates directly to `BTree.insert`
* Automatically triggers node splits when needed

---

#### `get(byte[] key)`

* Performs a point lookup
* Traverses the B+ tree from root to leaf
* Returns:

  * Stored value if present
  * `null` if key does not exist or was deleted

---

#### `delete(byte[] key)`

* Implements **logical deletion**
* Inserts the key with a `null` value
* Physical space reclamation is deferred to future compaction / freelist logic

This mirrors how real databases handle deletes.

---

#### `scan()`

* Performs an ordered range scan
* Uses leaf node chaining (`nextLeaf`)
* Returns all keys in sorted order
