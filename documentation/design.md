# Design Overview

This document describes the internal design of **JBase**, a disk-backed key–value storage engine implemented in Java.
The system focuses on **correctness, persistence, and clarity of data structure invariants**, rather than feature breadth.

---

## Design Goals

The project is guided by the following goals:

1. **Persistent Storage**
   All data structures must survive process restarts without relying on in-memory state.

2. **Predictable I/O**
   Reads and writes operate on fixed-size pages to ensure stable disk behavior.

3. **Clear Separation of Concerns**
   Storage, indexing, and API layers are strictly separated.

4. **Structural Correctness**
   Tree invariants are explicitly enforced to prevent silent corruption.

---

## High-Level Architecture

```
┌────────────┐
│  KVStore   │   User-facing API
└─────┬──────┘
      │
┌─────▼──────┐
│   BTree    │   Index structure (B+ Tree)
└─────┬──────┘
      │
┌─────▼──────┐
│ PageStore  │   Abstract page allocator
└─────┬──────┘
      │
┌─────▼──────┐
│ FileStore  │   Disk-backed page storage
└────────────┘
```

Each layer communicates only through explicit interfaces.
No object references cross persistence boundaries.

---

## Page-Based Storage Model

### Page Abstraction

All data is stored in fixed-size pages:

```
Page
┌──────────────────────────┐
│ Page ID                  │
│ Raw byte[4096]           │
└──────────────────────────┘
```

* Pages are addressed by integer page IDs
* No dynamic resizing or partial writes
* All persistence happens at page granularity

This model mirrors real database storage engines.

---

## Metadata Management

### MetaPage

Page `0` is reserved for global metadata.

```
MetaPage
┌───────────────┐
│ rootPageId    │
└───────────────┘
```

Responsibilities:

* Persist the current root of the B+ tree
* Enable full recovery after restart
* Prevent accidental reuse of page 0 as a data node

The root page is **never** allowed to be page 0.

---

## Index Structure: B+ Tree

JBase uses a **B+ Tree** rather than a binary tree for predictable depth and efficient range scans.

### Node Types

#### Internal Node

```
keys:     [K1 | K2 | K3]
children: [P0 | P1 | P2 | P3]   (page IDs)
```

* Stores keys only for navigation
* Children are page IDs, not object references
* Always maintains `children = keys + 1`

#### Leaf Node

```
keys:      [K1 | K2 | K3]
values:    [V1 | V2 | V3]
nextLeaf → pageId
```

* Stores actual key–value pairs
* Maintains sorted order
* Linked to adjacent leaves for fast scans

---

## Tree Invariants

The following invariants are strictly enforced:

* Keys are always sorted
* Internal nodes never store values
* Leaf nodes never store children
* `children.size() == keys.size() + 1` for internal nodes
* No node may reference itself as a child
* Leaf nodes form a singly linked list

Violations result in immediate runtime errors rather than silent corruption.

---

## Insert Algorithm

Insertion follows standard B+ tree semantics:

1. Descend recursively to the target leaf
2. Insert key in sorted position
3. If overflow occurs:

   * Split node
   * Promote middle key
   * Propagate split upward
4. If root splits:

   * Create new root
   * Persist root page ID to MetaPage

All mutations are persisted page-by-page.

---

## Search Algorithm

Search proceeds top-down:

1. Binary search within node keys
2. Select appropriate child page
3. Repeat until leaf is reached
4. Perform final key comparison

No recursion relies on in-memory pointers.

---

## Range Scan

Range scans leverage leaf chaining:

1. Descend to leftmost leaf
2. Traverse `nextLeaf` pointers sequentially
3. Collect keys in sorted order

This avoids repeated tree traversal and provides linear scan performance.

---

## Serialization Format

Nodes are serialized directly into pages using a deterministic binary layout.

### Magic Header

Each page begins with a magic constant:

```
MAGIC = 0x4E4F4445  // "NODE"
```

Purpose:

* Detect uninitialized or corrupted pages
* Prevent accidental interpretation of garbage data
* Fail fast during debugging

### Layout Summary

```
[ MAGIC ]
[ isLeaf ]
[ keyCount ]
[ nextLeaf ]
[ key lengths + key bytes ]
[ values OR children ]
```

---

## Deletion Semantics

Deletes are implemented as **logical deletes**:

* A delete inserts the key with a `null` value
* Physical space reclamation is deferred
* Free list infrastructure exists for future reuse

This keeps tree structure stable while enabling correctness.

---

## Free List (Infrastructure)

A page-based free list tracks reusable pages:

* Stored as linked pages
* Each page stores multiple free page IDs
* Stack-style allocation

Currently implemented but not wired into production allocation.

---

## Persistence Guarantees

* All writes are synchronous at page level
* Tree structure is fully reconstructable from disk
* No hidden in-memory state is required
* Root pointer persistence ensures recoverability

Crash recovery requires only reopening the file.

---

## Design Tradeoffs

### Chosen

* Simplicity over maximum performance
* Explicit correctness checks
* Clear invariants and failure modes

### Not Chosen

* Write-ahead logging
* Concurrency control
* Compression or variable-length pages

These are intentionally excluded to keep the core storage engine focused and auditable.

