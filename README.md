# JBase – Disk-Backed Storage Engine ⚙️

![Java](https://img.shields.io/badge/Java-21-orange)
![Storage Engine](https://img.shields.io/badge/Type-Storage_Engine-blue)
![Data Structure](https://img.shields.io/badge/Data%20Structure-B%2B_Tree-green)
![Persistence](https://img.shields.io/badge/Persistence-Disk_Backed-red)
![Pages](https://img.shields.io/badge/Page_Size-4KB-lightgrey)
![Architecture](https://img.shields.io/badge/Architecture-Layered-purple)
![Design](https://img.shields.io/badge/Focus-System_Design-critical)
![Status](https://img.shields.io/badge/Status-Production_Ready-success)

---

**JBase** is a Java-based **disk-backed key–value storage engine** that implements a persistent **B+ tree index** over fixed-size pages.

It is designed as a **low-level storage system**, focusing on correctness, persistence, and data structure integrity rather than external database features.

---

## Project Scope

JBase implements the **core storage and indexing components** of a database engine:

* fixed-size page management
* disk-backed persistence
* B+ tree indexing
* sorted key access and range scans
* clean API separation

This project is intentionally limited to the **storage layer**, similar to the foundational components of systems like embedded databases.

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

Each layer is isolated via interfaces and communicates strictly through **page IDs and byte arrays**, ensuring clear separation of concerns.

---

## Storage Model

### Pages

* Fixed size: **4096 bytes (4KB)**
* Identified by a monotonically increasing page ID
* All disk I/O occurs at page granularity

```
Page ID → [ 4096 bytes ]
```

Pages act as the fundamental unit of persistence.

---

### Meta Page

Page `0` is reserved for global metadata.

```
Meta Page (Page 0)
┌────────────────────────┐
│ rootPageId (int)       │
└────────────────────────┘
```

The root page ID is persisted explicitly, enabling full reconstruction of the tree.

---

## B+ Tree Implementation

### Design Properties

* Balanced tree structure
* Keys stored in sorted order
* Values stored only in leaf nodes
* Internal nodes contain page references only
* Leaf nodes are linked for efficient range scans

---

### Internal Node Layout

```
keys:     [10 | 20 | 30]
children: [ 3 |  7 |  9 | 12 ]   (page IDs)
```

* Directs traversal
* Children count = keys + 1

---

### Leaf Node Layout

```
keys:     [10 | 12 | 15]
values:   [ A |  B |  C ]
nextLeaf: → page 14
```

* Stores actual key–value pairs
* Maintains sorted order
* Supports sequential access

---

### Leaf Chaining

```
Leaf → Leaf → Leaf → Leaf
```

Enables efficient range scans via sequential traversal.

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
│ values[] / children[]│
└──────────────────────┘
```

### Magic Header

Used to:

* detect uninitialized pages
* prevent invalid deserialization
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

Prevents unbounded file growth and enables efficient page reuse.

---

## Public API

### KVStore

```java
put(byte[] key, byte[] value)
get(byte[] key)
delete(byte[] key)
scan()
```

* `delete` is implemented as a logical delete
* `scan` returns sorted keys
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
* Tree fully reconstructable from disk
* Corruption detection via magic headers
* Root location explicitly persisted

---

## What This Project Demonstrates

This project demonstrates the ability to:

* design disk-backed data structures
* implement B+ tree indexing correctly
* manage persistent state explicitly
* serialize and deserialize complex structures
* reason about storage invariants
* debug tree consistency and corruption issues

---

## Author

**Pritam Acharya**

This project demonstrates practical understanding of **storage engines, indexing structures, and database internals**.
