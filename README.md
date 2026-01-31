# JBase

**JBase** is a Java-based **disk-backed key–value storage engine** that implements a persistent **B+ tree index** over fixed-size pages.

The project demonstrates a clean, layered design for building storage systems and focuses on correctness, persistence, and data structure integrity rather than external features.

---

## Project Scope

JBase implements the **core storage and indexing components** of a database engine:

* fixed-size page management
* disk-backed persistence
* B+ tree indexing
* sorted key access and range scans
* clean API separation

This project is intentionally limited to the storage layer.

---

## High-Level Architecture

```
┌────────────────────┐
│      KVStore       │
│  Public API Layer  │
│                    │
│ put / get / scan   │
└─────────▲──────────┘
          │
┌─────────┴──────────┐
│       BTree        │
│  Indexing Layer    │
│                    │
│ search / insert    │
│ split / rebalance  │
└─────────▲──────────┘
          │
┌─────────┴──────────┐
│     PageStore      │
│ Storage Abstraction│
└─────────▲──────────┘
          │
┌─────────┴──────────────┐
│   FileStore / MemStore │
│ Persistent / In-Memory │
└────────────────────────┘
```

Each layer is isolated by interfaces and communicates strictly through page IDs and byte arrays.

---

## Storage Model

### Pages

* Fixed size: **4096 bytes**
* Identified by a monotonically increasing page ID
* All disk I/O occurs at page granularity

```
Page ID → [ 4096 bytes ]
```

Pages are immutable units of storage from the perspective of higher layers.

---

### Meta Page

Page `0` is reserved for global metadata.

```
Meta Page (Page 0)
┌────────────────────────┐
│ rootPageId (int)       │
└────────────────────────┘
```

The B+ tree root page ID is stored here and updated atomically.

---

## B+ Tree Implementation

### Design Properties

* Balanced tree structure
* Keys stored in sorted order
* Values stored only in leaf nodes
* Internal nodes contain page references only
* Leaf nodes are linked for fast range scans

---

### Internal Node Layout

```
keys:     [10 | 20 | 30]
children: [ 3 |  7 |  9 | 12 ]   (page IDs)
```

* Directs traversal
* No data values stored
* Children count is always `keys + 1`

---

### Leaf Node Layout

```
keys:     [10 | 12 | 15]
values:   [ A |  B |  C ]
nextLeaf: → page 14
```

* Stores key–value pairs
* Maintains sorted order
* Linked to next leaf for sequential scans

---

### Leaf Chaining

```
Leaf → Leaf → Leaf → Leaf
```

Range scans are implemented as sequential traversal across leaf nodes.

---

## Node Serialization Format

Each B+ tree node is serialized into a single page.

```
┌──────────────────────┐
│ MAGIC HEADER (int)   │
│ isLeaf (byte)        │
│ keyCount (int)       │
│ nextLeaf (int)       │
│ keys[]               │
│ values[] or children[]│
└──────────────────────┘
```

### Magic Header

A fixed magic value is written at the start of each node page to:

* detect uninitialized pages
* prevent accidental misinterpretation of raw data
* fail fast on corruption

---

## Page Allocation and Reuse

### Free List

Released pages are tracked using a page-based free list.

```
FreeListPage
┌──────────────────────┐
│ nextFreeListPage     │
│ count                │
│ freedPageIds[]       │
└──────────────────────┘
```

This prevents unbounded file growth and mirrors how production storage engines manage disk space.

---

## Public API

### KVStore

```java
put(byte[] key, byte[] value)
get(byte[] key)
delete(byte[] key)
scan()
```

* `delete` is implemented as a logical delete (value set to null)
* `scan` returns keys in sorted order
* All operations are persisted immediately

---

## Operation Flow

### Insert Path

```
KVStore.put
  → BTree.insert
    → descend to leaf
      → insert key
        → split if overflow
          → propagate split upward
```

### Lookup Path

```
KVStore.get
  → BTree.search
    → binary search keys
      → follow child pointers
        → return value from leaf
```

---

## Data Integrity Guarantees

* Deterministic page layout
* No object references stored on disk
* Tree structure fully reconstructable from disk state
* Corruption detection via magic headers
* Root location persisted explicitly

---

## What This Project Demonstrates

This project demonstrates the ability to:

* design disk-backed data structures
* implement B+ tree indexing correctly
* manage persistent state explicitly
* serialize and deserialize complex structures
* reason about storage invariants
* debug recursive tree corruption issues

---

## Author

**Pritam Acharya**

This project was built to demonstrate practical understanding of storage engines, indexing structures, and low-level database internals.

---

